import java.awt.Color;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.Random;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable; 
import net.sourceforge.jFuzzyLogic.plot.JDialogFis;

/////////////////////////////////////////////////////////////// FuzzyChase //////////

public class FuzzyChase {

    public static void main(String[] args) throws IOException {

        JFrame dFrame = new JFrame();
        dFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dFrame.setSize(800, 800);
        dFrame.setBackground(Color.WHITE);
        DrawingPanel dPanel = new DrawingPanel();
        dPanel.setBackground(new Color(73, 207, 86));
        Container c = dFrame.getContentPane();
        c.add(dPanel);
        dFrame.setVisible(true);
        Timer timer = new Timer( );
        DrawingTask dTask = new DrawingTask(dPanel);
        timer.schedule(dTask, 0, 10);
    }

}

/////////////////////////////////////////////////////////////// DrawingTask //////////

class DrawingTask extends TimerTask {

    JPanel panel;
    BufferedImage img1;
    BufferedImage img2;

    int xPos1;
    int yPos1;
    int xPos2;
    int yPos2;
    int rho;
    double phi1;
    double phi2;

    double speed1;
    double speed2;
    double speed_change;

    double[] tab_speed;
    int[] tab_interval;
    int speed_idx;
    int interval;

    FIS fis;
    Random generator;

    DrawingTask(JPanel panel) throws IOException{

        this.panel = panel;
        img1 = ImageIO.read(new File("car3.jpg"));
        img2 = ImageIO.read(new File("car4.jpg"));

        xPos1 = 700;
        yPos1 = 400;
        rho = 300;
        phi1 = 0;

        xPos2 = 700;
        yPos2 = 400;
        phi2 = -1.5;
    	speed2 = 0.001;
        speed_change = 0;

        interval = 3000;

        tab_speed=new double[] { 0.001, 0.002, 0.003, 0.005, 0.008, 0.01, 0.012, 0.015 }; 	
        tab_interval=new int[] { 1000, 2000, 3000, 4000 };	  			
        generator = new Random();

		String fileName = "./tempomat.fcl";

		fis = FIS.load(fileName, true);
		
		if( fis == null ) { 
				System.err.println("Nie moge zaladowc pliku: '" + fileName + "'");
				return;
			}
    }

    @Override
    public void run() {





        interval = interval - 10;
        if (interval <=0){
            speed_idx = Math.abs(generator.nextInt()%tab_speed.length);
            int interval_idx = Math.abs(generator.nextInt()%tab_interval.length);
            interval = tab_interval[interval_idx];
        }

		speed1 = tab_speed[speed_idx]; 

        Graphics g = panel.getGraphics();

        g.clearRect(xPos1-15, yPos1-15, 40, 40);

        phi1 = phi1 + speed1;

        xPos1 = (int) (400 + rho * Math.cos(phi1));
        yPos1 = (int) (400 + rho * Math.sin(phi1));
        
        g.drawImage(img1, xPos1-15, yPos1-15, null);





	fis.setVariable("speed", speed2*10000);
	fis.setVariable("distance",(phi1-phi2)*100);
	fis.evaluate();

	speed_change=fis.getVariable("acceleration").getValue();
	
	System.out.println(String.format("S1: %8.4f\t  S2: %8.4f\t D:%8.4f\t => P:%8.4f", speed1*10000, speed2*10000, (phi1-phi2)*100, speed_change)); 

	speed2 = speed2 + speed2*(speed_change/100);

    g.clearRect(xPos2-15, yPos2-15, 40, 40);
	phi2 = phi2 +speed2;
    xPos2 = (int) (400 + rho * Math.cos(phi2));
    yPos2 = (int) (400 + rho * Math.sin(phi2));
    g.drawImage(img2, xPos2-15, yPos2-15, null);
    }
}

/////////////////////////////////////////////////////////////// DrawingPanel //////////


class DrawingPanel extends JPanel{

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillOval(50, 50, 700, 700);
        g.setColor(new Color(73, 207, 86));
        g.fillOval(150, 150, 500, 500);
    }
}

