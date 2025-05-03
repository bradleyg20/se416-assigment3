// ---------------------------------------------------------------------
// 1) Dispatch table: place it as a static field of XSSFWorkbook
// ---------------------------------------------------------------------


private static final Map<
        Class<?>,
        BiConsumer<XSSFWorkbook,RelationPart>> DISPATCH = Map.ofEntries(

    Map.entry(SharedStringsTable.class , (wb,r) ->
        wb.sharedStringSource = (SharedStringsTable) r.getDocumentPart()),

    Map.entry(StylesTable.class        , (wb,r) ->
        wb.stylesSource       = (StylesTable)       r.getDocumentPart()),

    Map.entry(ThemesTable.class        , (wb,r) -> {
        // use a one-element holder because the local ‘theme’ var
        // in onDocumentRead() is not accessible here
        ThemesTable th = (ThemesTable) r.getDocumentPart();
        wb.setTheme(th);          // you may have to write this helper
    }),

    Map.entry(CalculationChain.class   , (wb,r) ->
        wb.calcChain = (CalculationChain) r.getDocumentPart()),

    Map.entry(MapInfo.class            , (wb,r) ->
        wb.mapInfo  = (MapInfo)        r.getDocumentPart()),

    Map.entry(XSSFSheet.class          , (wb,r) -> {
        XSSFSheet sh = (XSSFSheet) r.getDocumentPart();
        wb.tmpSheetIdMap.put(r.getRelationship().getId(), sh);
    }),

    Map.entry(ExternalLinksTable.class , (wb,r) -> {
        ExternalLinksTable el = (ExternalLinksTable) r.getDocumentPart();
        wb.tmpExtLinksIdMap.put(r.getRelationship().getId(), el);
    })
);

@Override
protected void onDocumentRead() throws IOException {
    try {
        WorkbookDocument doc =
            WorkbookDocument.Factory.parse(getPackagePart().getInputStream(),
                                           DEFAULT_XML_OPTIONS);
        this.workbook = doc.getWorkbook();

        // temp maps used later in the method
        tmpSheetIdMap     = new HashMap<>();
        tmpExtLinksIdMap  = new HashMap<>();

        for (RelationPart rp : getRelationParts()) {
            DISPATCH
              .getOrDefault(
                     rp.getDocumentPart().getClass(),
                     (wb,r)->logger.debug("Unhandled part {}",
                            r.getDocumentPart().getClass().getSimpleName()))
              .accept(this, rp);
        }

        boolean packageReadOnly =
            getPackage().getPackageAccess() == PackageAccess.READ;

        boolean packageReadOnly = (getPackage().getPackageAccess() == PackageAccess.READ);

        if (stylesSource == null) {
            // Create Styles if it is missing
            if (packageReadOnly) {
                stylesSource = new StylesTable();
            } else {
                stylesSource = (StylesTable)createRelationship(XSSFRelation.STYLES, this.xssfFactory);
            }
        }
        stylesSource.setWorkbook(this);
        stylesSource.setTheme(theme);

        if (sharedStringSource == null) {
            // Create SST if it is missing
            if (packageReadOnly) {
                sharedStringSource = new SharedStringsTable();
            } else {
                sharedStringSource = (SharedStringsTable)createRelationship(XSSFRelation.SHARED_STRINGS, this.xssfFactory);
            }
        }

        // Load individual sheets. The order of sheets is defined by the order
        //  of CTSheet elements in the workbook
        sheets = new ArrayList<>(shIdMap.size());
        //noinspection deprecation
        for (CTSheet ctSheet : this.workbook.getSheets().getSheetArray()) {
            parseSheet(shIdMap, ctSheet);
        }

        // Load the external links tables. Their order is defined by the order
        //  of CTExternalReference elements in the workbook
        externalLinks = new ArrayList<>(elIdMap.size());
        if (this.workbook.isSetExternalReferences()) {
            for (CTExternalReference er : this.workbook.getExternalReferences().getExternalReferenceArray()) {
                ExternalLinksTable el = elIdMap.get(er.getId());
                if(el == null) {
                    logger.log(POILogger.WARN, "ExternalLinksTable with r:id " + er.getId()+ " was defined, but didn't exist in package, skipping");
                    continue;
                }
                externalLinks.add(el);
            }
        }

        // Process the named ranges
        reprocessNamedRanges();
    } catch (XmlException e) {
        throw new POIXMLException(e);
    }
}
