package Modelo;

import java.awt.Dimension;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class Enemigo extends Thread
{
    private Random random = new Random();
    private BufferedImage actual;
    private BufferedImage[] imagenes = new BufferedImage[4];
    private int pos_x, pos_y, separacion, moverX, moverY;
    private boolean continuar = true, choque = false;
    private Dimension dim;
    
    public Enemigo(Dimension tam, int s, ArrayList<Enemigo> enemigo, int max)
    {
        try
        {
            actual = ImageIO.read(new File("imagenes/fantasma.png"));
            
            for(int n= 0 ; n< 4 ; n++)
                imagenes[n] = actual.getSubimage(n * 100, 0, 100, 99);
            
            actual = imagenes[0];
        }
        catch(IOException e)
        {
            System.err.println("Imagen no encontrada.");
        }
        dim = tam;
        separacion = s;
        
        do
        {
            switch(random.nextInt(4))
            {
                case 0: //Aparecerá arriba
                    pos_y = separacion;
                    pos_x = random.nextInt(dim.width - actual.getWidth() +1);
                    break;
                case 1: //Aparecerá abajo
                    pos_y = dim.height - actual.getHeight();
                    pos_x = random.nextInt(dim.width - actual.getWidth() + 1);
                    break;
                case 2: //Aparecerá a la derecha
                    pos_y = random.nextInt(dim.height - actual.getHeight() - separacion +1)  +separacion;
                    pos_x = dim.width - actual.getWidth();
                    break;
                case 3: //Aparecerá izquierda
                    pos_y = random.nextInt(dim.height - actual.getHeight() - separacion +1)  + separacion;
                    pos_x = 0;
                    break;
            }
            
            choque = false;
            
            for(int n=0 ; n<max && !choque; n++)
                if(getBounds().intersects(enemigo.get(n).getBounds()))
                    choque = true;
        }while(choque);
        
        moverX = velocidadAleatoria();
        moverY = velocidadAleatoria();
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
        pos_x += moverX;
        pos_y += moverY;
        
        if(pos_x < 0 || pos_x + actual.getWidth() > dim.width)
        {
            moverX *= -1;
        }
        
        if(pos_y < separacion || pos_y + actual.getHeight()> dim.height)
        {
            moverY *= -1;
        }
        
        if(moverX > 0)
        {
            if(moverY > 0)
                actual = imagenes[3];
            else
                actual = imagenes[2];
        }
        else
            if(moverY > 0)
                actual = imagenes[1];
            else
                actual = imagenes[0];
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
    
    public void rebote()
    {
        moverX *= -1;
        moverY *= -1;
    }
    
    public BufferedImage getImage()
    {
        return actual;
    }
    
    public Rectangle getBounds()
    {
            return new Rectangle(pos_x, pos_y, actual.getWidth(), actual.getHeight());
    }
    
    private int velocidadAleatoria()
    {
        if(random.nextInt(2) == 0) //Positivo. De -5 a -15
            return random.nextInt(11) - 15;
        else
            return random.nextInt(11) + 5; //Negativo. de 5 a15
    }
}