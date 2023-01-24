# sbxbam2pe

## Build Requirement in SC1

- module load jdk/11.0.2
- module load maven/3.5.0


## Build

1. git clone git@ghe-rss.roche.com:kimh89/sbxbam2pe.git
2. got sbxbam2pe
3. mvn clean package

- bam2pe.jar will be located at ./target dir.
- bam2pe.jar is executalbe and can be deployed without dependencies.

## Run

### Usage
```
java -jar ./target/bam2pe.jar -h
Unknown option: '-h'
Usage: <main class> [-i=<inputBam>] [-l=<maxReadLength>] [-o=<outputDir>]
                    [-p=<outputPrefix>]
  -i, --input-bam=<inputBam>
         input BAM file path
  -l, --max-read-length=<maxReadLength>
         maximum read length of R1 and R2
  -o, --output-dir=<outputDir>
         output directory
  -p, --prefix=<outputPrefix>
         prefix of the output R1 and R2 files
```

### Examples

1. output current directory

Since the default prefix of outputs is "PE_", PE_R1.fastq and  PE_R2.fastq will be generated in the current directory.

```
java -jar ./target/bam2pe.jar -i /sc1/groups/onco/Analysis/bioinfo_analyses/kimh89/SBX/bfx-ngs-AVC-225/pipeline/examples/SC1/output_demux2samples_10k_params/analysis/cfDNA7/mergeBam/cfDNA7.merged.bam
```

2. output a different directory with different prefix.

From the following command, sorted_R1.fastq and sorted_R2.fastq will be generated in ./target dir.

```
java -jar ./target/bam2pe.jar -p sorted_ -o ./target -i /sc1/groups/onco/Analysis/bioinfo_analyses/kimh89/SBX/bfx-ngs-AVC-225/pipeline/examples/SC1/output_demux2samples_10k_params/analysis/cfDNA7/mergeBam/cfDNA7.merged.bam
```



