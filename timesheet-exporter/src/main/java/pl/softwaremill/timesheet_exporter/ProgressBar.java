package pl.softwaremill.timesheet_exporter;

/**
 * source http://nakkaya.com/2009/11/08/command-line-progress-bar/
 */
public class ProgressBar {

    public static void printProgBar(int percent){
        StringBuilder bar = new StringBuilder("[");

        for(int i = 0; i < 50; i++){
            if( i < (percent/2)){
                bar.append("=");
            }else if( i == (percent/2)){
                bar.append(">");
            }else{
                bar.append(" ");
            }
        }

        bar.append("]   " + percent + "%     ");
        System.err.print("\r" + bar.toString());
    }
}
