package nominas;

import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Nominas {

    static final String RUTA_EXCEL = "../resources/SistemasInformacionII.xlsx";
    static final String RUTA_XML_DNI = "../resources/Errores.xml";
    static final String RUTA_XML_CCC = "../resources/erroresCCC.xml";
    
    public static void main(String[] args) throws IOException, InvalidFormatException, TransformerException, ParserConfigurationException, ParseException{
        
       Excel practicaExcel = new Excel(RUTA_EXCEL,0);
       practicaExcel.corregirDNI();
       practicaExcel.completarIBAN();
       practicaExcel.completarEmail();
       practicaExcel.cerrarExcel();
       
       XML practicaXML = new XML(RUTA_XML_DNI, RUTA_XML_CCC, practicaExcel);
       practicaXML.crearXMLDNI();
       practicaXML.crearXMLCCC();
       
       NominaTrabajador practicaNomina = new NominaTrabajador(RUTA_EXCEL, 1, practicaExcel);
       practicaNomina.obtenerNominas();
       practicaNomina.cerrarExcel();
       
       
       BaseDeDatos bbdd = new BaseDeDatos(practicaExcel, practicaNomina);
       bbdd.actualizarBaseDeDatos();
       bbdd.cerrarSesion();
       
   }
}