# [Anchor Removal Operator for EcmaScript Regular Expressions](https://github.com/sdbs-uni-p/EcmaScript2Brics)

This project contains a program to translate a regular expression from [ECMAScript](https://www.ecma-international.org/publications-and-standards/standards/ecma-262/) 
syntax into the one used in the Java library [dk.brics.automaton](https://www.brics.dk/automaton/index.html).  
Another tool is the evaluation of JSON files containing patterns in ECMAScript syntax.

## Authors
Algorithm design: [Dominik Freydenberger](https://www.lboro.ac.uk/departments/compsci/staff/academic-teaching/dominik-freydenberger/) (Loughborough University)  
Implementation: Christoph Köhnen (University of Passau)

## Installation and Build Process
To install and build this program, type the following:
```shell
git clone https://github.com/sdbs-uni-p/EcmaScript2Brics
cd EcmaScript2Brics
./build-dependencies.sh
```

## Usage of the program
For a regular expression ``<regExp>`` the brics expression can be obtained with the following line:
```shell
java -jar EcmaScript2Brics.jar "<regExp>"
```
The expression must be typed in unescaped and the result is java-escaped, i.e. ``\n`` does not cause a linebreak by printing the output.

### Example
For a pattern ``^\\d$`` from a JSON document the unescaped version is ``^\d$``.
The command lines look as follows:
```shell
java -jar EcmaScript2Brics.jar "^\d$"
The Brics expression is: [0-9]
```

## Citation
To refer to this project in a publication, please use this BibTeX entry.
```bibtex
@Misc{anchor_removal,
  author =  {Dominik Freydenberger and Christoph K\"ohnen and Stefanie Scherzinger},
  title =   {Anchor Removal Operator for ECMAScript Regular Expressions},
  note =    {\url{https://github.com/sdbs-uni-p/EcmaScript2Brics}},
  year =    2021
}
```

## Acknowledgement
This work was partly funded by Deutsche Forschungsgemeinschaft (DFG, German Research Foundation) grant #385808805.