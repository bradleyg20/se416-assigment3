1. **Elaboration of Class Names**
   - **What change you made:**  
     Elaborate class names like DoC.java to DaggerOfCrescendoWeapon.java and EqType.java to EquipmentType.java
   - **Why you made the change:**  
     It makes the inheritance of classes clearer.
   - **Impact of the change:**  
     It increases readability by allowing users to better understand some of the inheritance within the code without opening multiple files.

2. **Optimize Character.setCoreModifiers**
   - **What change you made:**  
     Change the switch statement from 17 cases to 9 cases by changing the switch variable.
   - **Why you made the change:**  
     Due to the nature of the switch variable, every two consecutive numbers (e.g., 7 and 8) had the same result. By dividing the original variable by two, we reduce duplicate cases as well as reduce jumps in the assembly.
   - **Impact of the change:**  
     It increases the performance of the method.

3. **Remove duplication from Character.setCoreModifiers**
   - **What change you made:**  
     Remove duplicate calls to the nextInt function of the Random class. Also remove the duplicate code for getting the largest 2 out of 3 values. Replace them with Array methods.
   - **Why you made the change:**  
     Code duplication can cause issues if any part of the code is change in one place but not in others.
   - **Impact of the change:**  
     It improves code readability.