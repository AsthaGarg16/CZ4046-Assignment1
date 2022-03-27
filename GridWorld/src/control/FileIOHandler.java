package control;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import entity.Utility_Action;
import entity.constants;

public class FileIOHandler
{
    public static void writeToFile(List<Utility_Action[][]> lstUtilitys, String fileName, int scale) {

        StringBuilder sb = new StringBuilder();
        String pattern = "00.000";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        for (int col = 0; col < constants.NUM_COLS*scale; col++) {
            for (int row = 0; row < constants.NUM_ROWS*scale; row++) {

                Iterator<Utility_Action[][]> iter = lstUtilitys.iterator();
                while(iter.hasNext()) {

                    Utility_Action[][] actionUtil = iter.next();
                    sb.append(decimalFormat.format(
                            actionUtil[col][row].getUtil()).substring(0, 6));

                    if(iter.hasNext()) {
                        sb.append(",");
                    }
                }
                sb.append("\n");
            }
        }

        writeToFile(sb.toString().trim(), fileName + ".csv");
    }

    public static void writeToFile(String content, String fileName)
    {
        try
        {
            FileWriter fw = new FileWriter(new File(fileName), false);

            fw.write(content);
            fw.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    public static void writeToTxt(String configInfo, boolean isValue, int scale)
    {
        try
        {
            String file1="config_results_value_scale_"+scale+".txt";
            String file2="config_results_policy_scale_"+scale+".txt";
            String filename = isValue?file1:file2;
            FileWriter fw = new FileWriter(new File(filename), false);

            fw.write(configInfo);
            fw.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}