package Modelo;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class Pacman extends Thread
{
    private BufferedImage actual;
    private BufferedImage[] imagenes = new BufferedImage[9];
    private int pos_x, pos_y, separacion;
    private boolean continuar = true, vivo = true, abrir = true;
    private Dimension dim;
    private MouseEvent evento = null;
    
    public Pacman(Dimension tam, int s)
    {
        try
        {
            actual = ImageIO.read(new File("imagenes/pacman.png"));
            
            for(int n=0 ; n< 9;n++)
                imagenes[n] = actual.getSubimage(n * 100, 0, 100, 100);
            
            actual = imagenes[0];
        }
        catch(IOException e)
        {
            System.err.println("Imagen no encontrada.");
        }
        
        pos_x = tam.width/2;
        pos_y = tam.height/2;
        dim = tam;
        separacion = s;
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
        if(vivo)
        {
            if(abrir)
            {
                if(actual == imagenes[0])
                    actual = imagenes[1];
                else if(actual == imagenes[1])
                {
                    actual = imagenes[2];
                    abrir = false;
                }
            }
            else
            {
                if(actual == imagenes[2])
                    actual = imagenes[1];
                else if(actual == imagenes[1])
                {
                    actual = imagenes[0];
                    abrir = true;
                }
            }

            if(evento != null)
            {
                pos_x = evento.getX();
                pos_y = evento.getY();

                if(pos_x + actual.getWidth()/2 > dim.width)
                    pos_x = dim.width - actual.getWidth() / 2;
                else
                    if(pos_x - actual.getWidth()/2 < 0)
                        pos_x = actual.getWidth() / 2;

                if(pos_y + actual.getHeight()/2 > dim.height)
                    pos_y = dim.height - actual.getHeight()/ 2;
                else
                    if(pos_y - actual.getHeight()/2 < separacion)
                        pos_y = actual.getHeight()/2 + separacion;
            }
        }
        else
        {
            if(actual == imagenes[0])
                actual = imagenes[1];
            else if(actual == imagenes[1])
                actual = imagenes[2];
            else if(actual == imagenes[2])
                actual = imagenes[3];
            else if(actual == imagenes[3])
                actual = imagenes[4];
            else if(actual == imagenes[4])
                actual = imagenes[5];
            else if(actual == imagenes[5])
                actual = imagenes[6];
            else if(actual == imagenes[6])
                actual = imagenes[7];
            else if(actual == imagenes[7])
                actual = imagenes[8];
        }
    }
    
    public void recogerEventoRaton(MouseEvent e)
    {
        evento = e;
    }

    private void pausa(int tiempo)
    {
        try
        {
            Thread.sleep(tiempo);
        }
        catch (InterruptedException ignorada) { }
    }
    
    public boolean finMuerte()
    {
        if(actual == imagenes[8])
            return true;
        else
            return false;
    }
            
    
    public int getX()
    {
        return pos_x;
    }
    
    public int getY()
    {
        return pos_y;
    }
    
    public BufferedImage getImage()
    {
        return actual;
    }
    
    public Rectangle getBounds()
    {
            return new Rectangle(pos_x - actual.getWidth()/2, pos_y - actual.getHeight()/2, actual.getWidth(), actual.getHeight());
    }
    
    public void morir()
    {
        vivo = false;
    }
}