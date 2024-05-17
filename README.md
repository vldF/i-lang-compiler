### I-Lang Compiler

This is a simple compiler of I-Lang. I-Lang is Imperative language looks pascal-like

[There is a project presentation](/media/I-Lang.pdf)

## How to run tests

1. Open project in IntelliJ Idea
2. Use run presets:
   ![img.png](/media/img.png)

or

1. Fork the project
2. Run
   ```bash
   ./gradlew test
   ```

## How to build the project

Run: 
```bash
./gradlew assembleDist
```

Archives will be created at path `./build/distributions/`. Unpack them and 
use run scripts in `bin` directory

```
$ ./i-lang-compiler --help

Arguments:
source path -> path to source ilang file { String }
Options:
--output, -o [a.out] -> path for compiled binary file { String }
--help, -h -> Usage info
```

## How to run compiled file

Just run the build file :)

You need to pass a starting routine name ant its arguments:

```bash
./a.out fib 10
# expected output is 55
```
