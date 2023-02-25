package nominas;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XML  {
    
    //private final String RutaExcel;
    private final String RutaXMLDNI;
    private final String RutaXMLCCC;
    //private final int HojaExcel;
    
    private Excel excel;
    
    public XML(String rutaXMLDNI, String rutaXMLCCC, Excel excel) {
        //this.RutaExcel = rutaExcel;
        //this.HojaExcel = hojaExcel;
        this.RutaXMLDNI = rutaXMLDNI;
        this.RutaXMLCCC = rutaXMLCCC;
        
        this.excel = excel;
    }
    
    public void crearXMLDNI() throws ParserConfigurationException, TransformerConfigurationException, TransformerException{
        
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = (Document) documentBuilder.newDocument();
        
        Element root = document.createElement("Trabajadores"); //creamos el elemento que engloba a todos los demás
        document.appendChild(root); //y lo aniadimos al xml
        
        //escribimos en el xml los empleados cuyo DNI este vacío
        for(int i = 0; i < excel.getListaDNI().size(); i++){
            if(excel.getListaDNI().get(i) == null){
                Element idEmpleado = document.createElement("Trabajador");
                root.appendChild(idEmpleado); //al root le ponemos el hijo idEmpleado y le ponemos el atributo Id, que es la fila de excel que corresponde
                Attr attr = document.createAttribute("id");
                attr.setValue(Integer.toString(i+2));
                idEmpleado.setAttributeNode(attr);
                
                
                Element nombre = document.createElement("Nombre");
                if(excel.getListaNombre().get(i) != null){ //si existe el nombre lo aniadimos al elemento nombre
                    nombre.appendChild(document.createTextNode(excel.getListaNombre().get(i).toString()));
                }else{
                    nombre.appendChild(document.createTextNode(""));
                }
                idEmpleado.appendChild(nombre); // y el elemento nombre lo aniadimos a idEmpleado, igual que los demás elementos
                
               
                Element apellido1 = document.createElement("PrimerApellido");
                if(excel.getListaApellido1().get(i) != null){
                    apellido1.appendChild(document.createTextNode(excel.getListaApellido1().get(i).toString()));
                }else{
                    apellido1.appendChild(document.createTextNode(""));
                }
                idEmpleado.appendChild(apellido1);
                

                Element apellido2 = document.createElement("SegundoApellido");
                if(excel.getListaApellido2().get(i) != null){
                    apellido2.appendChild(document.createTextNode(excel.getListaApellido2().get(i).toString()));
                }else{
                    apellido2.appendChild(document.createTextNode(""));
                }
                idEmpleado.appendChild(apellido2);
                
                Element categoria = document.createElement("Categoria");
                if(excel.getListaCategoria().get(i) != null){
                    categoria.appendChild(document.createTextNode(excel.getListaCategoria().get(i).toString()));
                }else{
                    categoria.appendChild(document.createTextNode(""));
                }
                idEmpleado.appendChild(categoria);

                Element empresa = document.createElement("Empresa");
                if(excel.getListaEmpresa().get(i) != null){
                    empresa.appendChild(document.createTextNode(excel.getListaEmpresa().get(i).toString()));
                }else{
                    empresa.appendChild(document.createTextNode(""));
                }
                idEmpleado.appendChild(empresa);
            }
        }
        
        excel.DNIRepetidos(); //para que rellene la lista de DNI repetidos
       //escribimos en el XML los empleados que estén repetidos 
       for(int i = 0; i < excel.getListaDNIRepetidos().size(); i++){
           
            Element idEmpleado = document.createElement("Trabajador");
            root.appendChild(idEmpleado);
            Attr attr = document.createAttribute("id");
            attr.setValue(Integer.toString(excel.getListaDNIPosiciones().get(i) + 2));
            idEmpleado.setAttributeNode(attr);


            Element nombre = document.createElement("Nombre");
            if(excel.getListaNombre().get(excel.getListaDNIPosiciones().get(i)) != null){
                nombre.appendChild(document.createTextNode(excel.getListaNombre().get(excel.getListaDNIPosiciones().get(i)).toString()));
            }else{
                nombre.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(nombre);


            Element apellido1 = document.createElement("PrimerApellido");
            if(excel.getListaApellido1().get(excel.getListaDNIPosiciones().get(i)) != null){
                apellido1.appendChild(document.createTextNode(excel.getListaApellido1().get(excel.getListaDNIPosiciones().get(i)).toString()));
            }else{
                apellido1.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(apellido1);


            Element apellido2 = document.createElement("SegundoApellido");
            if(excel.getListaApellido2().get(excel.getListaDNIPosiciones().get(i)) != null){
                apellido2.appendChild(document.createTextNode(excel.getListaApellido2().get(excel.getListaDNIPosiciones().get(i)).toString()));
            }else{
                apellido2.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(apellido2);

            Element categoria = document.createElement("Categoria");
            if(excel.getListaCategoria().get(excel.getListaDNIPosiciones().get(i)) != null){
                categoria.appendChild(document.createTextNode(excel.getListaCategoria().get(excel.getListaDNIPosiciones().get(i)).toString()));
            }else{
                categoria.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(categoria);

            Element empresa = document.createElement("Empresa");
            if(excel.getListaEmpresa().get(excel.getListaDNIPosiciones().get(i)) != null){
                empresa.appendChild(document.createTextNode(excel.getListaEmpresa().get(excel.getListaDNIPosiciones().get(i)).toString()));
            }else{
                empresa.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(empresa);
       }
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(RutaXMLDNI));
        
        transformer.transform(domSource, streamResult);
 
    }
    
    public void crearXMLCCC() throws ParserConfigurationException, TransformerConfigurationException, TransformerException{
        
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = (Document) documentBuilder.newDocument();
        
        Element root = document.createElement("cuentas");
        document.appendChild(root);
        
        //escribimos en el xml los empleados cuyo CCC sea erroneo
        for(int i = 0; i < excel.getListaIndicesCCCErroneos().size(); i++){

            Element idEmpleado = document.createElement("cuenta");
            root.appendChild(idEmpleado);
            Attr attr = document.createAttribute("id");
            attr.setValue(Integer.toString(excel.getListaIndicesCCCErroneos().get(i)+2));
            idEmpleado.setAttributeNode(attr);


            Element nombre = document.createElement("Nombre");
            if(excel.getListaNombre().get(excel.getListaIndicesCCCErroneos().get(i)) != null){
                nombre.appendChild(document.createTextNode(excel.getListaNombre().get(excel.getListaIndicesCCCErroneos().get(i)).toString()));
            }else{
                nombre.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(nombre);


            Element apellido1 = document.createElement("PrimerApellido");
            if(excel.getListaApellido1().get(excel.getListaIndicesCCCErroneos().get(i)) != null){
                apellido1.appendChild(document.createTextNode(excel.getListaApellido1().get(excel.getListaIndicesCCCErroneos().get(i)).toString()));
            }else{
                apellido1.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(apellido1);


            Element apellido2 = document.createElement("SegundoApellido");
            if(excel.getListaApellido2().get(excel.getListaIndicesCCCErroneos().get(i)) != null){
                apellido2.appendChild(document.createTextNode(excel.getListaApellido2().get(excel.getListaIndicesCCCErroneos().get(i)).toString()));
            }else{
                apellido2.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(apellido2);


            Element empresa = document.createElement("Empresa");
            if(excel.getListaEmpresa().get(excel.getListaIndicesCCCErroneos().get(i)) != null){
                empresa.appendChild(document.createTextNode(excel.getListaEmpresa().get(excel.getListaIndicesCCCErroneos().get(i)).toString()));
            }else{
                empresa.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(empresa);
            
            Element CCC = document.createElement("CCC");
            if(excel.getListaCCCErroneos() != null){
                CCC.appendChild(document.createTextNode(excel.getListaCCCErroneos().get(i).toString()));
            }else{
                CCC.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(CCC);
            
            Element IBAN = document.createElement("IBAN");
            if(excel.getListaIBAN().get(excel.getListaIndicesCCCErroneos().get(i)) != null){
                IBAN.appendChild(document.createTextNode(excel.getListaIBAN().get(excel.getListaIndicesCCCErroneos().get(i))));
            }else{
                IBAN.appendChild(document.createTextNode(""));
            }
            idEmpleado.appendChild(IBAN);
            
        }
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(RutaXMLCCC));
        
        transformer.transform(domSource, streamResult);
 
    }
}
