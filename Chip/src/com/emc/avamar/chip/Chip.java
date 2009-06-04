package com.emc.avamar.chip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chip {

      public static List<String> getDirContents(String dir) {
            List<String> list = new ArrayList<String>();

            File fdir = new File(dir);
            if (!fdir.isDirectory()) {
                  System.out.println(dir + " is not a directory, exiting");
                  System.exit(1);
            }
            String files[] = fdir.list();

            for (String file : files) {
                  File f = new File(dir + "/" + file);
                  if (f.isFile()) {
                        list.add(dir + "/" + file);
                  } else if (f.isDirectory()) {
                        //System.out.println("\\-> " + dir + "/" + file);
                        list.addAll(getDirContents(dir + "/" + file));
                  }
            }

            return list;
      }

      public static void writeToLocation(byte[] data, String filename) {
            
            File f = new File(filename);

            //String s = "I was here!\n";
            //byte data[] = s.getBytes();
            //ByteBuffer out = ByteBuffer.wrap(data);
            //File file = new File(filename);

            //ByteBuffer copy = ByteBuffer.allocate(12);
            //FileChannel fc = null;

            //try {
                  //fc = (FileChannel)file.newByteChannel(READ, WRITE);

                  ////Read the first 12 bytes of the file.
                  //int nread;
                  //do {
                        //nread = fc.read(copy);
                  //} while (nread != -1 && copy.hasRemaining());

                  ////Write "I was here!" at the beginning of the file.
                  //fc.position(0);
                  //while (out.hasRemaining())
                        //fc.write(out);
                  //out.rewind();

                  ////Move to the end of the file.  Copy the first 12 bytes to
                  ////the end of the file.  Then write "I was here!" again.
                  //long length = fc.size();
                  //fc.position(length-1);
                  //copy.flip();
                  //while (copy.hasRemaining())
                        //fc.write(copy);
                  //while (out.hasRemaining())
                        //fc.write(out);
            //} catch (IOException x) {
                  //System.out.println("I/O Exception: " + x);
            //} finally {
                  ////Close the file.
                  //if (fc != null) fc.close();
                  //System.out.println(file + " has been modified!");
            //}
      }

      /**
       * writeDataRandomly will write the data to the RandomAccessFile, note
       * that the file is not opened OR closed by this function.
       */
      public static void writeDataRandomly(RandomAccessFile raf) throws Exception {
            Random rand = new Random();
            String token = Long.toString(Math.abs(rand.nextLong()), 36);
            token += Long.toString(Math.abs(rand.nextLong()), 36);
            token += Long.toString(Math.abs(rand.nextLong()), 36);
            //System.out.println("[+] " + token + ";");
            if ((raf.length() - token.length()) < 0) {
                  System.out.println("File too small to write data to.");
                  return;
            }
            int offset = rand.nextInt((int)(raf.length() - token.length()));

            raf.seek(offset);
            raf.writeUTF(token);
      }

      /**
       * @param args
       */

      public static String getRandomFilename(List<String> files) {
            Random rand = new Random();
            return files.get(rand.nextInt(files.size()));
      }

      public static void main(String[] args) {
            System.out.println("Welcome to CHIP => CHurn In Place");

            if (args.length != 2) {
                  System.out.println("Usage: ./chip <directory> <churn rate>");
                  return;
            }

            List<String> filenames = getDirContents(args[0]);

            double rate = (Double.parseDouble(args[1]) / 100.0);
            int total_files = filenames.size();
            System.out.println("Change rate: " + rate*100 + "% for " + total_files + " files.");
            int changed_files = (int)(total_files * rate);
            System.out.println("Files to be changed: " + changed_files);

            System.out.println("\n*** WARNING! Files in '" + args[0] + "' will be changed/corrupted! Do NOT use this tool on important data! ***");
            System.out.println("You have 5 seconds to press 'ctrl+c' to cancel...");
            try {
                  Thread.sleep(5000);
            } catch (Exception e1) {
                  System.out.println("Whew! Exiting...");
                  return;
            }
            System.out.println("Okay, assuming you want to go ahead and go through with it, churning...");

            int i = 0;
            while (i < changed_files) {
                  String file = getRandomFilename(filenames);
                  //System.out.println("Random file: " + file);
                  try {
                        RandomAccessFile raf = new RandomAccessFile(file, "rw");
                        // Let's write some random data 3 times
                        writeDataRandomly(raf);
                        writeDataRandomly(raf);
                        writeDataRandomly(raf);

                        raf.close();
                  } catch (Exception e) {
                        e.printStackTrace();
                  }
                  i++;
            }

            System.out.println("Done.");

      }

}
