Build IOTA MAM from here:
https://github.com/thibault-martinez/entangled/

Change the POM includePaths to the output of your bazel.
Then add the generated mam library to your path.

Terminal:
`export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/path/to/libfolder`

Eclipse:

Run configurations: Environment -> New 
- LD_LIBRARY_PATH /path/to/libfolder


TADA