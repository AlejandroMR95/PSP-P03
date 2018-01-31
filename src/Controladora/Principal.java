package Controladora;

import Modelo.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.BufferedImage;
//import  sun.audio.*;
//import  java.io.*;

public class Principal extends javax.swing.JFrame implements Runnable, MouseMotionListener
{
    private final String textoInicio="PULSE ENTER PARA EMPEZAR A JUGAR", textoPuntos="Puntos: ", textoPuntosTotal="Puntuacion total: ";
    private final String textoNivel="Nivel: ", completado="JUEGO COMPLETADO", textoReiniciar="PULSE ENTER PARA REINICIAR EL NIVEL", textoJuego="PACMAN 2.0";
    private final String textoGanar="HAS GANADO", textoPerder="HAS PERDIDO", textoContinuar="PULSE ENTER PARA CONTINUAR", textoFPS="FPS: ";
    private final int separacion = 100, maxNivel = 3;
    private int puntos, puntosTotal = 0,nivel = 1, maxComida = 5, cuentaComida, maxEnemigo = 3;
    private int fase = 0; //0: inicio. 1 Intermedio.  entre 2: Juego. 3: Fin.
    private boolean ganar = false, vivo = true;
    private Image offscreen;
    private Graphics2D bufferGraphics;
    private Dimension dim;
    private BufferedImage fondo;
    private Pacman pacman;
    private ArrayList <Enemigo> enemigo = new ArrayList();
    private ArrayList <Comida> comida = new ArrayList();
    private Font titulo = new Font("titulo",Font.BOLD,40);
    private Font puntuacion = new Font("puntuacion",Font.ITALIC,25);
    private FontMetrics fm;
    private boolean continuar = true;
    //private AudioStream musicaFondo;
    private long tiempoInicio =0;
    
    public Principal()
    {
        initComponents();
        /*
        try
        {
            InputStream in = new FileInputStream("sonidos/cancion.wav");
            musicaFondo = new AudioStream(in);   
        }
        catch(IOException e)
        {}*/

        setTitle("Pacman 2.0");
        setSize(1000, separacion + (800));
        dim = getSize();
        this.setResizable(false);
        fondo = getFondo();
        
        this.addKeyListener(new java.awt.event.KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e) //Si se ha pulsado una tecla del teclado.
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    switch(fase)
                    {
                        case 0:
                            fase = 2;
                            inicializarElementos();
                            break;
                        case 1:
                            fase = 2;
                            if(ganar)
                                nivel++;
                            inicializarElementos();
                            break;
                    }
                }
            }
        });
        addMouseMotionListener(this);
        
        //AudioPlayer.player.start(musicaFondo);
    }
    
    @Override
    public void run()
    {
        while(continuar)
        {
            setBackground(Color.GRAY);
            offscreen = createImage(dim.width, dim.height);
            bufferGraphics = (Graphics2D) offscreen.getGraphics();
            repaint();
            tiempoInicio = System.currentTimeMillis();
            
            pausa(50);
        }
    }

    public void paint(Graphics g) 
    {
        if(bufferGraphics!=null)
        {
            bufferGraphics.clearRect(0,0,dim.width,dim.height); //Borra todos los elementos
            
            switch(fase)
            {
                case 0: //Inicio
                    bufferGraphics.setColor(Color.yellow);
                    bufferGraphics.setFont(titulo);
                    fm = bufferGraphics.getFontMetrics();
                    escribirTextoCentrado(bufferGraphics, textoJuego, fm.stringWidth(textoJuego), dim.width/2, dim.height/4);
                    escribirTextoCentrado(bufferGraphics, textoInicio, fm.stringWidth(textoInicio), dim.width/2, dim.height/2);
                    break;
                case 1: //Intermedio
                    bufferGraphics.setColor(Color.yellow);
                    bufferGraphics.setFont(titulo);
                    fm = bufferGraphics.getFontMetrics();
                    escribirTextoCentrado(bufferGraphics, textoNivel+nivel, fm.stringWidth(textoNivel+nivel), dim.width/2, dim.height/4);
                    if(ganar)
                    {
                        bufferGraphics.setColor(Color.GREEN);
                        escribirTextoCentrado(bufferGraphics, textoGanar, fm.stringWidth(textoGanar), dim.width/2, dim.height/3);
                        bufferGraphics.setColor(Color.yellow);
                        escribirTextoCentrado(bufferGraphics, textoContinuar, fm.stringWidth(textoContinuar), dim.width/2, dim.height/2);
                    }
                    else
                    {
                        bufferGraphics.setColor(Color.red);
                        escribirTextoCentrado(bufferGraphics, textoPerder, fm.stringWidth(textoPerder), dim.width/2, dim.height/3);
                        bufferGraphics.setColor(Color.yellow);
                        escribirTextoCentrado(bufferGraphics, textoReiniciar, fm.stringWidth(textoReiniciar), dim.width/2, dim.height/2);
                    }
                    break;
                case 2: //Juego
                    bufferGraphics.drawImage(fondo, 0, 0, null); //Dibuja la imagen de fondo.

                    bufferGraphics.setColor(Color.GRAY);
                    bufferGraphics.fillRect(0, 0, dim.width, separacion);

                    bufferGraphics.setColor(Color.yellow);
                    bufferGraphics.setFont(puntuacion);
                    fm = bufferGraphics.getFontMetrics();
                    escribirTextoCentrado(bufferGraphics, textoNivel+nivel, fm.stringWidth(textoNivel+nivel), dim.width/2, separacion - 40);
                    escribirTextoCentrado(bufferGraphics, textoPuntos+puntos, fm.stringWidth(textoPuntos+puntos), dim.width/2, separacion - 15);

                    bufferGraphics.drawImage(pacman.getImage(), pacman.getX() - pacman.getImage().getWidth() / 2, pacman.getY()- pacman.getImage().getHeight()/ 2, null);

                    for(int n=0 ; n< cuentaComida; n++)
                    {
                        bufferGraphics.setColor(comida.get(n).getColor());
                        bufferGraphics.fill(comida.get(n).forma());

                        if(vivo)
                        {
                            if(comida.get(n).forma().intersects(pacman.getBounds()))
                            {
                                puntos += 100;
                                cuentaComida--;
                                comida.remove(n);
                                n--;
                            }
                            else
                                for(int m = n+1; m < cuentaComida; m++)
                                    if(comida.get(n).forma().intersects(comida.get(m).forma().getBounds()))
                                    {
                                        comida.get(n).rebote();
                                        comida.get(m).rebote();
                                    }
                        }
                    }

                    for(int n=0 ; n< maxEnemigo; n++)
                    {
                        bufferGraphics.drawImage(enemigo.get(n).getImage(), enemigo.get(n).getX(), enemigo.get(n).getY(), null);
                        
                        if(vivo && enemigo.get(n).getBounds().intersects(pacman.getBounds()))
                        {
                            vivo = false;
                            pararHilos();
                            pacman.morir();
                        }
                    }   
                    
                    
                    if(cuentaComida == 0)
                    {
                        ganar = true;
                        puntosTotal+=puntos;
                        if(nivel == maxNivel)
                            fase = 3;
                        else
                            fase = 1;
                        cerrarHilos();
                    }
                    else
                        if(!vivo && pacman.finMuerte())
                        {
                            ganar = false;
                            fase = 1;
                            cerrarHilos();
                        }
                    
                    break;
                case 3: //Final
                    bufferGraphics.setColor(Color.yellow);
                    bufferGraphics.setFont(titulo);
                    fm = bufferGraphics.getFontMetrics();
                    escribirTextoCentrado(bufferGraphics, completado, fm.stringWidth(completado), dim.width/2, dim.height/3);
                    escribirTextoCentrado(bufferGraphics, textoPuntosTotal+puntosTotal, fm.stringWidth(textoPuntosTotal+puntosTotal), dim.width/2, dim.height/2);
                    break;
            }
            
            bufferGraphics.setColor(Color.yellow);
            bufferGraphics.setFont(puntuacion);
            fm = bufferGraphics.getFontMetrics();
            try
            {
                escribirTextoCentrado(bufferGraphics, textoFPS+1000/(System.currentTimeMillis()-tiempoInicio), fm.stringWidth(textoFPS), dim.width - 180, separacion - 40);
            }
            catch(Exception e)
            {
                escribirTextoCentrado(bufferGraphics, textoFPS+1000, fm.stringWidth(textoFPS), dim.width - 180, separacion - 40);
            }            
            g.drawImage(offscreen,0,0,this); //Dibuja todo.
        }
    }
    
    private void escribirTextoCentrado(Graphics g, String line, int lineW, int x, int y)
    {
        g.drawString(line, x - lineW / 2, y);//center
    }
    
    private void inicializarElementos()
    {
        vivo = true;
        puntos = 0;
        
        if(ganar)
        {
            maxEnemigo += 2;
            maxComida += 2;
        }
        else
            ganar = true;
        
        cuentaComida = maxComida;
        
        for(int n=0 ; n< maxEnemigo; n++)
        {
            enemigo.add(new Enemigo(dim, separacion, enemigo, n));
            enemigo.get(n).start();
        }
        
        for(int n=0 ; n< maxComida; n++)
        {
            comida.add(new Comida(dim, separacion, comida, n));
            comida.get(n).start();
        }
        
        pacman = new Pacman(dim, separacion);
        pacman.start();
    }
    
    private void pararHilos()
    {
        for(int n=0 ; n< cuentaComida; n++)
            comida.get(n).stop();
        
        for(int n=0 ; n< maxEnemigo; n++)
            enemigo.get(n).stop();
    }
    
    private void cerrarHilos()
    {
        enemigo.clear();
        comida.clear();
        pacman.stop();
    }
    
    private BufferedImage getFondo() //Hace que la foto de fondo tenga el tamaño deseado.
    {
        BufferedImage bi = null;
        
        try
        {
            ImageIcon ii = new ImageIcon("imagenes/fondo.jpg");
            bi = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) bi.createGraphics();
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY));
            g2d.drawImage(ii.getImage(), 0, 0, dim.width, dim.height, null);
        }
        catch (Exception e)
        {
            System.err.println("Imagen de fondo no encontrada.");
            return null;
        }
        
        return bi;
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    void pausa(int tiempo) //realiza una pausa de un tiempo determinado.
    {
        try
        {
            Thread.sleep(tiempo);
        }
        catch (InterruptedException ignorada) { }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[])
    {
        Principal principal = new Principal();
        principal.setVisible(true);
        principal.run();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(fase == 2)
            pacman.recogerEventoRaton(e);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        if(fase == 2)
            pacman.recogerEventoRaton(e);
    }
}