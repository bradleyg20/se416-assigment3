/**
 * Creates a deep copy of an existing sheet, delegating each
 * logical task to a compact helper for clarity and testability.
 *
 * @param sourceIndex 0-based index of the sheet to clone
 * @param requestedName optional name for the clone (null --> auto-generate)
 * @return the newly created sheet
 * @throws IllegalSheetIndexException   (unchecked) if the index is invalid
 * @throws NotSuitableSheetNameException (unchecked) if the supplied name is illegal
 * @throws POIXMLException              on I/O or OPC errors
 */
public XSSFSheet cloneSheet(int sourceIndex,
                            @Nullable String requestedName) {

    /*  1. locate & name  */
    XSSFSheet src = fetchSheet(sourceIndex);
    String  name  = decideName(src, requestedName);
    XSSFSheet dst = createSheet(name);

    /*  2. copy low-level structures  */
    copyPackageRelations(src, dst);
    copyExternalLinks    (src, dst);
    copySheetXml         (src, dst);

    /*  3. fix unsupported objects  */
    stripUnsupportedParts(dst);

    /*  4. copy the (optional) drawing  */
    copyDrawing(src, dst);

    dst.setSelected(false); // clone must not steal focus
    return dst;
}

/*                              helpers
 * ==================================================================== */

/**
 * Returns the sheet for the given index after delegating to the built-in
 * validation utility, ensuring callers never receive null or out-of-range
 * indices.
 *
 * @param index position of the desired sheet
 * @return located sheet
 * @throws IllegalSheetIndexException if the index is out of bounds
 */
private XSSFSheet fetchSheet(int index)
        throws IllegalSheetIndexException {
    validateSheetIndex(index); // POI utility
    return sheets.get(index); // never null here
}


/**
 * Computes the final name for the clone: validates a caller-supplied name
 * or produces a unique derivative of the source name when none is given.
 *
 * @param src original sheet
 * @param requestedName optional explicit name
 * @return legal, unique sheet name
 * @throws NotSuitableSheetNameException if the supplied name is invalid
 */
private String decideName(XSSFSheet src,
                          @Nullable String requestedName)
        throws NotSuitableSheetNameException {

    if (requestedName == null) {
        return getUniqueSheetName(src.getSheetName());
    }
    validateSheetName(requestedName); // POI utility
    return requestedName;
}

/**
 * Copies every internal package relationship except the drawing
 * (handled separately) to keep workbook structure intact.
 *
 * @param src source sheet
 * @param dst destination sheet
 */
private void copyPackageRelations(XSSFSheet src,
                                  XSSFSheet dst) {
    for (RelationPart rp : src.getRelationParts()) {
        POIXMLDocumentPart part = rp.getDocumentPart();
        if (part instanceof XSSFDrawing) continue; // postpone
        addRelation(rp, dst);
    }
}

/**
 * Duplicates all external OPC relationships so that hyperlinks and other
 * external references continue to resolve in the clone.
 *
 * @param src source sheet
 * @param dst destination sheet
 * @throws POIXMLException if OPC rejects the new relationship
 */
private void copyExternalLinks(XSSFSheet src,
                               XSSFSheet dst) {

    try {
        for (PackageRelationship rel
                : src.getPackagePart().getRelationships()) {

            if (rel.getTargetMode() == TargetMode.EXTERNAL) {
                dst.getPackagePart().addExternalRelationship(
                        rel.getTargetURI().toASCIIString(),
                        rel.getRelationshipType(),
                        rel.getId());
            }
        }
    } catch (InvalidFormatException e) {
        throw new POIXMLException("Failed to clone external links", e);
    }
}

/**
 * Performs an in-memory round-trip of the sheet XML to create a byte-for-byte
 * copy inside the destination sheet, preserving all worksheet data.
 *
 * @param src source sheet
 * @param dst destination sheet
 * @throws POIXMLException on stream or read/write failures
 */
private void copySheetXml(XSSFSheet src,
                          XSSFSheet dst) {

    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
        src.write(bos);
        try (ByteArrayInputStream bis =
                     new ByteArrayInputStream(bos.toByteArray())) {
            dst.read(bis);
        }
    } catch (IOException ex) {
        throw new POIXMLException("Failed to copy sheet XML", ex);
    }
}


/**
 * Removes XML fragments that Apache POI cannot yet replicate safely
 * (legacy comments and page-setup nodes) and logs a warning once.
 *
 * @param sheet sheet to sanitise
 */
private void stripUnsupportedParts(XSSFSheet sheet) {
    CTWorksheet ct = sheet.getCTWorksheet();

    if (ct.isSetLegacyDrawing()) {
        logger.log(POILogger.WARN,
                   "Cloning sheets with comments is not yet supported.");
        ct.unsetLegacyDrawing();
    }
    if (ct.isSetPageSetup()) {
        logger.log(POILogger.WARN,
                   "Cloning sheets with page setup is not yet supported.");
        ct.unsetPageSetup();
    }
}

/**
 * Replicates the sheet’s drawing—including XML and OPC relationships—
 * ensuring shapes, charts and images appear identically in the clone.
 *
 * @param src source sheet
 * @param dst destination sheet
 */
private void copyDrawing(XSSFSheet src,
                         XSSFSheet dst) {

    XSSFDrawing srcDrawing = src.getDrawingPatriarch();
    if (srcDrawing == null) return; // nothing to do

    CTWorksheet ct = dst.getCTWorksheet();
    if (ct.isSetDrawing()) { // remove stub created by POI
        ct.unsetDrawing();
    }

    XSSFDrawing dstDrawing = dst.createDrawingPatriarch();
    dstDrawing.getCTDrawing().set(srcDrawing.getCTDrawing());

    /* copy drawing-level relationships */
    List<RelationPart> srcRels = srcDrawing.getRelationParts();
    for (RelationPart rp : srcRels) {
        addRelation(rp, dstDrawing);
    }
}
