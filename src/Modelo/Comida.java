package Modelo;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Comida extends Thread
{
    private Random random = new Random();
    private int pos_x, pos_y, separacion, moverX, moverY, tamAncho, tamAlto, forma;
    private boolean continuar = true, choque;
    private Dimension dim;
    private Color color;
    
    public Comida(Dimension tam, int s, ArrayList<Comida> comida, int max)
    {
        dim = tam;
        separacion = s;
        
        tamAncho = random.nextInt(40)+30;
        tamAlto = random.nextInt(40)+30;
        
        do
        {
            switch(random.nextInt(4))
            {
                case 0: //Aparecerá arriba
                    pos_y = separacion;
                    pos_x = random.nextInt(dim.width - tamAncho +1);
                    break;
                case 1: //Aparecerá abajo
                    pos_y = dim.height - tamAlto;
                    pos_x = random.nextInt(dim.width - tamAncho + 1);
                    break;
                case 2: //Aparecerá a la derecha
                    pos_y = random.nextInt(dim.height - tamAlto - separacion +1)  +separacion;
                    pos_x = dim.width - tamAncho;
                    break;
                case 3: //Aparecerá izquierda
                    pos_y = random.nextInt(dim.height - tamAlto - separacion +1)  + separacion;
                    pos_x = 0;
                    break;
            }
            
            choque = false;
            
            for(int n=0 ; n<max && !choque; n++)
                if(forma().getBounds().intersects(comida.get(n).forma().getBounds()))
                    choque = true;
        }while(choque);
        
        forma = random.nextInt(2);
        
        int rgb = Color.HSBtoRGB(random.nextFloat(),(float)0.5,(float)0.5);
        color = new Color(rgb);
        
        moverX = velocidadAleatoria();
        moverY = velocidadAleatoria();
    }
    
    public RectangularShape forma()
    {
        if(forma == 1)
            return new Rectangle2D.Double(pos_x,pos_y,tamAncho,tamAlto);
        else
            return new Ellipse2D.Double(pos_x,pos_y,tamAncho,tamAlto);
    }
    
    public void run()
    {
        while(continuar)
        {
            mover();
            pausa(50);
        }
    }
    
    public void mover()
    {
        if(pos_x < 0 || pos_x + tamAncho > dim.width)
        {
            moverX *= -1;
        }
        
        if(pos_y < separacion || pos_y + tamAlto> dim.height)
        {
            moverY *= -1;
        }
        
        pos_x += moverX;
        pos_y += moverY;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public void rebote()
    {
        moverX *= -1;
        moverY *= -1;
        
        pos_x += moverX;
        pos_y += moverY;
    }

    private void pausa(int tiempo)
    {
        try
        {
            Thread.sleep(tiempo);
        }
        catch (InterruptedException ignorada) { }
    }
    
    public int getX()
    {
        return pos_x;
    }
    
    public int getY()
    {
        return pos_y;
    }
    
    private int velocidadAleatoria()
    {
        if(random.nextInt(2) == 0) //Positivo. De -5 a -10
            return random.nextInt(6) - 10;
        else
            return random.nextInt(6) + 5; //Negativo. de 5 a 10
    }
}