
package filtros.instagram;

import org.opencv.core.Core;

/* Tutorial de instalación: https://www.youtube.com/watch?v=YAIQagMAyoQ (9:34); sirve 
para Apache Netbeans 15 aunque en el tutorial se use Netbeans 8.2
Comando para agregar .dll: -Djava.library.path="C:\opencv-460\build\java\x64"
*/

/**
 *
 * @author: github.com/Samvel24
 */
public class FiltrosInstagram
{
    public static void main(String[] args) 
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("Version de OpenCV:" + Core.VERSION);
        
        Efectos e = new Efectos();
        e.configuracionCamara();
    }
}
