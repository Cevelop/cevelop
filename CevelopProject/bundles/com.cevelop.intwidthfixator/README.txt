What this Plugin should be able to do:

Allow for definition of the address width of the target platform (8BIt / 16Bit / 32BIt 64Bit).
	- This could be done using a project property.
	- If the cross compiling toolset is known possibly the target platform could be extracted.
	
Offer the user an action to convert all the used int-types (char / short int / int / long int / long long int) to the corresponding fixed width cstdint types.
	- Offer this as a do once menue action.
	- Offer a checker that translates typed ints on the fly in their fixed-with counterpart.
	
Show warnings if the user declares types that are wider than the address width of the target platform.
	- Offer a checker that sets off warnings.
	
If the used type is wider than the declared address width, the type will be converted to the biggest available cstdint type.
If the declared type is "unsigned", the replacement type should be of type std::uintXX_t.
If the declared type is a plain char without signed/unsigned there must be a property to tell, if it should be converted to a std:int8_t / uint8_t (3.9.1)
If the resulting types are not wide enough, the compiler will show a warning that a narrowing-conversion will happen.

If there is not yet an include for <cstdint> it shall be created.

