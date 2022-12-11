
package filtros.instagram;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;

import org.opencv.highgui.ImageWindow;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class Efectos 
{
    public static Map<String, ImageWindow> ventanas = new HashMap<>();
    public int teclaPresionada;
    public boolean banderaResize;
    CascadeClassifier faceCascade;
    CascadeClassifier eyesCascade;
    
    public Efectos()
    {
        // Constructor
        banderaResize = false;
        faceCascade = new CascadeClassifier();
        eyesCascade = new CascadeClassifier();
        faceCascade.load("haarcascade_frontalface_alt.xml");
        eyesCascade.load("haarcascade_eye_tree_eyeglasses.xml");
    }
    
    public void configuracionCamara()
    {
        
        VideoCapture capture = new VideoCapture(0);
        Mat frame = Mat.zeros(new Size(640, 480), CvType.CV_8UC3);
        String nombreVentana = "Video";
        
        ImageWindow iW = new ImageWindow(nombreVentana, 0); // flag = 0 -->> WINDOW_NORMAL
        JFrame jFrame = new JFrame(nombreVentana);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /* Usamos StretchIcon para que la imagen de la cámara se visualice completamente
        dentro del JLabel (y de acuerdo al tamaño del JLabel) y de esta manera simular el 
        uso de las banderas:
        •WINDOW_KEEPRATIO
        •WND_PROP_ASPECT_RATIO
        descritas en https://docs.opencv.org/4.x/d0/d90/group__highgui__window__flags.html
        Nota 1: Hasta el momento, estas banderas no están diponibles en el paquete de 
        OpenCV 4.6 para Java, por ello se decidió agregar la clase StretchIcon
        Nota 2: Para saber más sobre StretchIcon ver:
        •https://stackoverflow.com/questions/14548808/scale-the-imageicon-automatically-to-label-size/34514866#34514866
        •https://tips4java.wordpress.com/2012/03/31/stretch-icon/
        •http://www.camick.com/java/source/StretchIcon.java
        */
        StretchIcon icon = new StretchIcon(HighGui.toBufferedImage(frame));
        JLabel jLabel = new JLabel(icon); // Área de visualización para una cadena de texto corta o una imagen, o ambos
        jLabel.setPreferredSize(new Dimension(640, 480));
        iW.setFrameLabelVisible(jFrame, jLabel);
        ventanas.put(nombreVentana, iW);
                
        if(!capture.isOpened()) {
            System.err.println("No se puede abrir la camara web");
            System.exit(0);
        }

        while(true) {
            capture.read(frame);
            if(frame.empty()) {
                break;
            }
            
            Core.flip(frame, frame, 1); // Volteo horizontal de la imagen -->> flipCode > 0
            deteccionyEfectos(frame);
            ImageWindow temp = ventanas.get(nombreVentana);
            temp.setMat(frame);
            icon = new StretchIcon(HighGui.toBufferedImage(temp.img));
            icon.proportionate = false;
            jLabel.setIcon(icon);
            
            mostrar();
        }
    }
    
    // Usamos una secuencia de procesos similar a la de HighGui.imshow() y HighGui.waitKey()
    private void mostrar() {
        // Si no hay ventanas para mostrar, salir
        if (ventanas.isEmpty()) {
            System.err.println("Ventanas vacias");
            System.exit(-1);
        }

        // Eliminar las ventanas no utilizadas
        Iterator<Map.Entry<String,
                ImageWindow>> iter = ventanas.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String,
                    ImageWindow> entry = iter.next();
            ImageWindow win = entry.getValue();
            if(win.alreadyUsed) {
                iter.remove();
                win.frame.dispose();
            }
        }
        
        // (if) Crear (else) Actualizar frame
        for (ImageWindow win : ventanas.values()) {
            if (win.img != null) {

                StretchIcon icon = new StretchIcon(HighGui.toBufferedImage(win.img));
                icon.proportionate = false;
                
                if (win.lbl == null) {
                    JFrame frame = HighGui.createJFrame(win.name, win.flag);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    JLabel lbl = new JLabel(icon);
                    
                    frame.addComponentListener(new ComponentListener() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            banderaResize = true;
                        }

                        @Override
                        public void componentMoved(ComponentEvent e) {
                            
                        }

                        @Override
                        public void componentShown(ComponentEvent e) {
                            
                        }

                        @Override
                        public void componentHidden(ComponentEvent e) {
                            
                        }
                    });
                    
                    if(banderaResize == true) {
                        banderaResize = false;
                        Imgproc.resize(win.img, win.img, new Size(win.frame.getWidth(), win.frame.getHeight()), 0, 0, Imgproc.INTER_LINEAR_EXACT);
                    }
                    win.setFrameLabelVisible(frame, lbl);
                } 
                else {
                    win.frame.addComponentListener(new ComponentListener() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            banderaResize = true;
                        }

                        @Override
                        public void componentMoved(ComponentEvent e) {
                            
                        }

                        @Override
                        public void componentShown(ComponentEvent e) {
                            
                        }

                        @Override
                        public void componentHidden(ComponentEvent e) {
                            
                        }
                    });
                    
                    if(banderaResize == true) {
                        banderaResize = false;
                        Imgproc.resize(win.img, win.img, new Size(win.frame.getWidth(), win.frame.getHeight()), 0, 0, Imgproc.INTER_LINEAR_EXACT);
                    }
                    win.lbl.setIcon(icon);
                }
            } 
            else {
                System.exit(-1);
            }
        }
        
        // Establecer todas las ventanas como ya utilizadas
        for (ImageWindow win : ventanas.values())
            win.alreadyUsed = true;
    }
    
    private void deteccionyEfectos(Mat frame)
    {
        String folder = "ImagenesEjemplo\\";
        /*Mat imagenAgregada = Imgcodecs.imread(folder + "gorra.png", 
                Imgcodecs.IMREAD_UNCHANGED);
        /*Mat imagenAgregada = Imgcodecs.imread(folder + "gorro_navidad.png", 
                Imgcodecs.IMREAD_UNCHANGED);*/
        /*Mat imagenAgregada = Imgcodecs.imread(folder + "gorro_azul_marino.png", 
                Imgcodecs.IMREAD_UNCHANGED);*/
        /*Mat imagenAgregada = Imgcodecs.imread(folder + "sombrero_cafe.png", 
                Imgcodecs.IMREAD_UNCHANGED);*/
        Mat imagenAgregada = Imgcodecs.imread(folder + "sombrero_gris.png", 
                Imgcodecs.IMREAD_UNCHANGED);
        
        /*Mat lentesAgregados = Imgcodecs.imread(folder + "lentesNegros.png", 
                Imgcodecs.IMREAD_UNCHANGED);*/
        /*Mat lentesAgregados = Imgcodecs.imread(folder + "lentesFiesta.png", 
                Imgcodecs.IMREAD_UNCHANGED);*/
        /*Mat lentesAgregados = Imgcodecs.imread(folder + "lentesVerdes.png", 
                Imgcodecs.IMREAD_UNCHANGED);*/
        /*Mat lentesAgregados = Imgcodecs.imread(folder + "lentesAzules.png", 
                Imgcodecs.IMREAD_UNCHANGED);*/
        Mat lentesAgregados = Imgcodecs.imread(folder + "lentes2023.png", 
                Imgcodecs.IMREAD_UNCHANGED);
       
        Mat frameGray = new Mat();
        Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);
        
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(frameGray, faces);
         
        Rect[] arrayOfFaces = faces.toArray();
        for(Rect face : arrayOfFaces) {
            //Imgproc.rectangle(frame, face, new Scalar(0, 255, 0), 3);
            
            Mat faceROI = frameGray.submat(face);

            // En cada rostro, detectamos los ojos
            MatOfRect eyes = new MatOfRect();
            eyesCascade.detectMultiScale(faceROI, eyes);

            List <Rect> listOfEyes = eyes.toList();
            
            /* Las siguientes variables nos servirán para crear un objeto de tipo Rect
            que nos permita establecer un rectángulo que contiene la zona de ambos ojos
            y en la cual se podrán colocar los lentes
            */
            int minX = 0;
            int maxX = 0;
            int minY = 0;
            int ancho2 = 0;
            Rect zonaLentes = new Rect(0, 0, 0, 0);
            
            // Si y solo si se han detectado los dos ojos
            if(listOfEyes.size() == 2) {
                int[] abscisas = new int[2]; // "X's""
                int[] ordenadas = new int[2]; // "Y's""
                int[] anchos = new int[2]; // width
                int idx = 0;
                
                /* entonces buscamos las coordenadas y anchos necesarios para establecer
                los atributos del objeto de tipo Rect
                */
                for (Rect eye : listOfEyes) {
                    abscisas[idx] = eye.x;
                    ordenadas[idx] = eye.y;
                    anchos[idx] = eye.width;
                    idx++;
                }
                
                if(abscisas[0] < abscisas[1]) {
                    minX = abscisas[0];
                    minY = ordenadas[0];
                    maxX = abscisas[1];
                    ancho2 = anchos[1];
                }
                else {
                    minX = abscisas[1];
                    minY = ordenadas[1];
                    maxX = abscisas[0];
                    ancho2 = anchos[0];
                }
                
                zonaLentes.x = face.x + minX;
                zonaLentes.width = (face.x +  maxX + ancho2) - (face.x + minX);
                zonaLentes.y = face.y + minY;
                // Se toma como ancho de la zona de lentes al primer ojo detectado (.get(0))
                zonaLentes.height = listOfEyes.get(0).height;
            }
            
            /*
            Imgproc.rectangle(frame, zonaLentes, 
                        new Scalar(255, 0, 0), 4);
            */
            /*
            Código para dibujar un rectángulo en cada ojo detectado ( colocar esta 
            instrucción dentro de un foreach -->> for(Rect eye : listOfEyes) ):
            Imgproc.rectangle(frame, new Rect((face.x + eye.x), 
                (face.y + eye.y), eye.width, eye.height), 
                    new Scalar(255, 0, 0), 4);
            */
            
            // Si y solo si se han detectado los dos ojos
            if(listOfEyes.size() == 2) {
                /* Tomamos la zona de los lentes (ambos ojos) y la colocamos en un
                nuevo objeto de tipo Mat (auxLentes)
                */
                Mat auxLentes = frame.submat(zonaLentes);
                Mat frameLentes = new Mat(auxLentes.rows(), auxLentes.cols(), auxLentes.type());
                auxLentes.copyTo(frameLentes);
                //Imgcodecs.imwrite(folder + "ResultLentes.jpg", auxLentes);
                
                /* Redimensionamos la imagen .png de los lentes que deseamos agregar
                al tamaño del objeto Mat 'auxLentes'
                */
                Mat lentesRedimensionados = new Mat();
                Imgproc.resize(lentesAgregados, lentesRedimensionados, 
                    new Size(frameLentes.width(), frameLentes.height()), 
                    0, 0,Imgproc.INTER_LINEAR_EXACT);
            
                
                 /* Determinamos la máscara que posee la imagen redimensionada de los
                lentes y la invertimos usando la operación bitwise_not()*/
                ArrayList <Mat> canalesLentes = new ArrayList<>();
                Core.split(lentesRedimensionados, canalesLentes);
                Mat mask = canalesLentes.get(3);
                Mat maskInv = new Mat();
                Core.bitwise_not(mask, maskInv);

                // Creamos una imagen con fondo negro y la imagen redimensionada
                Mat fondoNegro = new Mat();
                Core.bitwise_and(lentesRedimensionados, lentesRedimensionados, fondoNegro, mask);

                /* Luego creamos una imagen en donde en el fondo esté frame y en negro 
                la imagen redimensionada*/
                ArrayList <Mat> canalesFondoNegro1 = new ArrayList<>();
                Core.split(fondoNegro, canalesFondoNegro1);
                ArrayList <Mat> canalesFondoNegro2 = new ArrayList<>();
                for (int it = 0; it < canalesFondoNegro1.size() - 1; it++) {
                    canalesFondoNegro2.add(canalesFondoNegro1.get(it));
                }
                Mat fondoNegro2 = new Mat();
                Core.merge(canalesFondoNegro2, fondoNegro2);
                Mat frameFondo = new Mat();
                
                Core.bitwise_and(frameLentes, frameLentes, frameFondo, maskInv);
                
                // Sumamos las 2 imágenes obtenidas anteriormente
                Mat resultLentes = new Mat();
                Core.add(fondoNegro2, frameFondo, resultLentes);
                //Imgcodecs.imwrite(folder + "ResultLentes.jpg", resultLentes);
                
                int i, j;
                int m, n;
              
                /* Recorremos la imagen que contiene los lentes colocados en los ojos
                y la colocamos nuevamente en frame para que se pueda visualizar el 
                efecto deseado
                */
                for (i = zonaLentes.y, m = 0; i < (zonaLentes.y + zonaLentes.height) && m < resultLentes.rows(); i++, m++) {
                    for (j = zonaLentes.x, n = 0; j < (zonaLentes.x + zonaLentes.width) && n < resultLentes.cols(); j++, n++) {
                        double[] data = resultLentes.get(m, n);
                        frame.put(i, j, data);
                    }
                }
            }
            
            /* Redimensionamos la imagen que será agregada al video para que se adapte al
            tamanio del rostro (cuando el rostro se acerca a la cámara se ve más grande
            y cuando se aleja de la cámara se ve más pequeño)
            */
            Mat imagenRedimensionada = new Mat();
            Imgproc.resize(imagenAgregada, imagenRedimensionada, 
                    new Size(face.width, face.height), 0, 0,
                    Imgproc.INTER_LINEAR_EXACT);
            
            int dif = 0;
            
            /* Con la siguiente instrucción se devuelve el valor más grande (más 
            cercano a infinito positivo) que es menor o igual que el cociente algebraico
            Es decir, obtenemos un valor entero como resultado de dividir las filas de 
            la imagen redimensionada entre 3
            Esto es, obtener una porción del alto de la imagen de entrada 
            */
            int porcionAlto = Math.floorDiv(imagenRedimensionada.rows(), 3);
            Mat nuevoFrame;
            
            /*  Si existe suficiente espacio sobre el rostro detectado para insertar 
            la imagen de entrada redimensionada entonces se visualizará dicha imagen*/
            if((face.y - imagenRedimensionada.rows() + porcionAlto) >= 0) {
                // Tomamos la sección del frame en donde se va a ubicar el gorro/tiara
                Mat aux1 = frame.submat(face.y - imagenRedimensionada.rows() + porcionAlto, 
                        face.y + porcionAlto, face.x, face.x + imagenRedimensionada.cols());
                /* Para esta aplicación, es necesario asignar filas, columnas y tipo 
                a la matriz nuevoFrame con el objetivo de que copyTo() realice de 
                manera adecuada la copia de la submatriz aux1
                */
                nuevoFrame = new Mat(aux1.rows(), aux1.cols(), aux1.type());
                aux1.copyTo(nuevoFrame);
            }
            else {
                // Determinamos la sección de la imagen que excede a la del video
                dif = Math.abs(face.y - imagenRedimensionada.rows() + porcionAlto);
                
                // Tomamos la sección del frame en donde se va a ubicar el gorro/tiara
                Mat aux3 = frame.submat(0, face.y + porcionAlto, 
                        face.x, face.x + imagenRedimensionada.cols());
                /* Para esta aplicación, es necesario asignar filas, columnas y tipo 
                a la matriz nuevoFrame con el objetivo de que copyTo() realice de 
                manera adecuada la copia de la submatriz aux3
                */
                nuevoFrame = new Mat(aux3.rows(), aux3.cols(), aux3.type());
                aux3.copyTo(nuevoFrame);
            }
            
            /* Determinamos la máscara que posee la imagen redimensionada y la 
            invertimos usando la operación bitwise_not()*/
            ArrayList <Mat> canalesRedimensionada = new ArrayList<>();
            Core.split(imagenRedimensionada, canalesRedimensionada);
            Mat mask = canalesRedimensionada.get(3);
            Mat maskInv = new Mat();
            Core.bitwise_not(mask, maskInv);
            
            // Creamos una imagen con fondo negro y la imagen redimensionada
            Mat fondoNegro = new Mat();
            Core.bitwise_and(imagenRedimensionada, imagenRedimensionada, fondoNegro, mask);
            
            /* Luego creamos una imagen en donde en el fondo esté frame y en negro 
            la imagen redimensionada*/
            Mat fondoNegroAux = fondoNegro.submat(dif, fondoNegro.rows(), 
                    0, fondoNegro.cols());
            Mat fondoNegro2 = new Mat();
            fondoNegroAux.copyTo(fondoNegro2);
            ArrayList <Mat> canalesFondoNegro1 = new ArrayList<>();
            Core.split(fondoNegro2, canalesFondoNegro1);
            ArrayList <Mat> canalesFondoNegro2 = new ArrayList<>();
            for (int it = 0; it < canalesFondoNegro1.size() - 1; it++) {
                canalesFondoNegro2.add(canalesFondoNegro1.get(it));
            }
            Mat fondoNegro3 = new Mat();
            Core.merge(canalesFondoNegro2, fondoNegro3);
            Mat maskInvAux = maskInv.submat(dif, maskInv.rows(), 
                    0, maskInv.cols());
            Mat maskInvAux2 = new Mat();
            maskInvAux.copyTo(maskInvAux2);
            Mat frameFondo = new Mat();
            Core.bitwise_and(nuevoFrame, nuevoFrame, frameFondo, maskInvAux2);
           
            // Sumamos las 2 imágenes obtenidas anteriormente
            Mat result = new Mat();
            Core.add(fondoNegro3, frameFondo, result);
            
            /*  Nuevamente, este if aplica para el caso en que existe suficiente 
            espacio sobre el rostro detectado para insertar la imagen de entrada 
            redimensionada
            */
            if((face.y - imagenRedimensionada.rows() + porcionAlto) >= 0) {
                int i, j;
                int m, n;
                for (i = (face.y - imagenRedimensionada.rows() + porcionAlto), m = 0; i < (face.y + porcionAlto) && m < imagenRedimensionada.cols(); i++, m++) {
                    for (j = face.x, n = 0; j < (face.x + imagenRedimensionada.cols()) && n < imagenRedimensionada.rows(); j++, n++) {
                        double[] data = result.get(m, n);
                        frame.put(i, j, data);
                    }
                }  
            }
            else {
                int i, j;
                int m, n;
                for (i = 0, m = 0; i < (face.y + porcionAlto) && m < imagenRedimensionada.cols(); i++, m++) {
                    for (j = face.x, n = 0; j < (face.x + imagenRedimensionada.cols()) && n < imagenRedimensionada.rows(); j++, n++) {
                        double[] data = result.get(m, n);
                        frame.put(i, j, data);
                    }
                }
            }
        }
    }
}
