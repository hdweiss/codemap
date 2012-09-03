
#include <jni.h>
#include "main.h"
#include "options.h"
#include "routines.h"

jint
Java_com_hdweiss_codemap_Ctags_test()
{
    return 3;
}

jint
Java_com_hdweiss_codemap_Ctags_runmain()
{
    char* argv[] = {"ctags", "-h"};

    cookedArgs *args;
    setCurrentDirectory ();
	setExecutableName ("ctags");
 	checkRegex ();

	args = cArgNewFromArgv (argv);
	previewFirstOption (args);
	testEtagsInvocation ();
	initializeParsing ();
	initOptions ();
	readOptionConfiguration ();
//	verbose ("Reading initial options from command line\n");
	parseOptions (args);
	checkOptions ();
	makeTags (args);

    /*  Clean up.
	 */
	cArgDelete (args);
	freeKeywordTable ();
	freeRoutineResources ();
	freeSourceFileResources ();
	freeTagFileResources ();
	freeOptionResources ();
	freeParserResources ();
	freeRegexResources ();

	exit (0);
	return 0;

    return 4;
}
