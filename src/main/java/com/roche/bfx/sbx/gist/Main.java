package com.roche.bfx.sbx.gist;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;


public class Main implements Runnable{
    @CommandLine.Option(names = {"-i", "--input-bam"}, description = "input BAM file path")
    private File inputBam;

    @CommandLine.Option(names = {"-o", "--output-dir"}, description = "output directory", defaultValue = ".")
    private Path outputDir;

    @CommandLine.Option(names = {"-p", "--prefix"}, description = "prefix of the output R1 and R2 files", defaultValue = "PE_")
    private String outputPrefix;

    @CommandLine.Option(names = {"-l", "--max-read-length"}, description = "maximum read length of R1 and R2", defaultValue = "150")
    private int maxReadLength;


    public static void main(String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        final File file1 = new File(outputDir + "/" + outputPrefix+"R1.fastq.gz");
        final File file2 = new File(outputDir + "/" + outputPrefix+"R2.fastq.gz");

        try (SamReader reader = SamReaderFactory.makeDefault().open(inputBam)) {
            GZIPOutputStream gos1 = new GZIPOutputStream(new FileOutputStream(file1));
            GZIPOutputStream gos2 = new GZIPOutputStream(new FileOutputStream(file2));

            Iterator<SAMRecord> iter = reader.iterator();
            while (iter.hasNext()) {
                SAMRecord record = iter.next();
                String r1 = convertSAMRecordToR1(record);
                String r2 = convertSAMRecordToR2(record);
                gos1.write(r1.getBytes(StandardCharsets.UTF_8));
                gos2.write(r2.getBytes(StandardCharsets.UTF_8));
            }
            gos1.close();
            gos2.close();

        } catch (final Exception e) {
            System.out.println(e.getMessage());
            if(file1.exists()) file1.delete();
            if(file2.exists()) file2.delete();
            System.exit(1);
        }
    }

    /**
     * @param record
     * @return fastq string
     */
    private String convertSAMRecordToR1(SAMRecord record){
        StringBuilder sb = new StringBuilder();

        sb.append("@"+record.getReadName()).append(" 1:N:0").append("\n");
        if(record.getReadString().length() < maxReadLength) {
            sb.append(record.getReadString()).append("\n");
            sb.append("+").append("\n");
            sb.append(record.getBaseQualityString()).append("\n");
        }else{
            sb.append(record.getReadString().substring(0,maxReadLength)).append("\n");
            sb.append("+").append("\n");
            sb.append(record.getBaseQualityString().substring(0, maxReadLength)).append("\n");
        }
        return sb.toString();
    }

    /**
     * @param record
     * @return fastq string
     */
    private String convertSAMRecordToR2(SAMRecord record){
        StringBuilder sb = new StringBuilder();

        sb.append("@"+record.getReadName()).append(" 2:N:0").append("\n");
        if(record.getReadString().length() < maxReadLength) {
            sb.append(reverseComp(record.getReadString())).append("\n");
            sb.append("+").append("\n");
            sb.append(reverse(record.getBaseQualityString())).append("\n");
        }else{
            sb.append(reverseComp(record.getReadString().substring(record.getReadLength() - maxReadLength))).append("\n");
            sb.append("+").append("\n");
            sb.append(reverse(record.getBaseQualityString().substring(record.getReadLength()-maxReadLength))).append("\n");
        }
        return sb.toString();
    }

    private String reverseComp(String input){
        return complement(reverse(input));
    }

    private String reverse(String input){
        StringBuilder sb = new StringBuilder();
        sb.append(input);

        return sb.reverse().toString();
    }

    private String complement(String input){
        StringBuilder sb = new StringBuilder();
        char[] chars = input.toCharArray();
        for(char c : chars){
            switch(c) {
                case 'A':
                    sb.append('T');
                    break;
                case 'T':
                    sb.append('A');
                    break;
                case 'G':
                    sb.append('C');
                    break;
                case 'C':
                    sb.append('G');
                    break;
                case 'N':
                    sb.append('N');
                default:
                    break;
            }
        }

        return sb.toString();
    }

}


