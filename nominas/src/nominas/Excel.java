package nominas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Date;
import java.util.Locale;
public class Excel {
    
    private final String RutaExcel;
    private final int HojaExcel;
    
    private final int COLUMNA_NOMBRE = 0;
    private final int COLUMNA_APELLIDO1 = 1;
    private final int COLUMNA_APELLIDO2 = 2;
    private final int COLUMNA_DNI = 3;
    private final int COLUMNA_FECHA = 4;
    private final int COLUMNA_EMAIL = 5;
    private final int COLUMNA_EMPRESA = 6;
    private final int COLUMNA_CIF = 7;
    private final int COLUMNA_PRORRATEO = 8;
    private final int COLUMNA_CATEGORIA = 9;
    private final int COLUMNA_CODIGO_CUENTA = 10;
    private final int COLUMNA_PAIS = 11;
    private final int COLUMNA_IBAN = 12;
    
    private List<Cell> ListaDNI;//lista de celdas
    
    private List<Cell> ListaCuentas;
    private List<Cell> ListaPais;
    private List<String> ListaIBAN;
    
    private List<Cell> ListaNombre;
    private List<Cell> ListaApellido1;
    private List<Cell> ListaApellido2;
    private List<Cell> ListaEmpresa;
    private List<Cell> ListaCIF;
    private List<Cell> ListaCategoria;
    
    private List<Cell> ListaCCCErroneos;
    private List<Integer> ListaIndicesCCCErroneos;
    
    private List<Cell> ListaDNIRepetidos;
    private List<Integer> ListaDNIPosiciones;
    
    private List<Calendar> ListaFecha;
    private List<Cell> ListaProrrateo;
    
    private List<String> ListaEmail;
    
    private Sheet hoja;
    private XSSFWorkbook workbook;
    private FileInputStream file;
    
    public Excel(String rutaExcel, int hojaExcel) throws ParseException{
        this.RutaExcel = rutaExcel;
        this.HojaExcel = hojaExcel;
        
        ListaDNI = new ArrayList<Cell>();
        ListaCuentas = new ArrayList<Cell>();
        ListaPais = new ArrayList<Cell>();
        ListaIBAN = new ArrayList<String>();
        
        ListaNombre = new ArrayList<Cell>();
        ListaApellido1 = new ArrayList<Cell>();
        ListaApellido2 = new ArrayList<Cell>();
        ListaEmpresa = new ArrayList<Cell>();
        ListaCIF = new ArrayList<Cell>();
        ListaCategoria = new ArrayList<Cell>();
        
        ListaCCCErroneos = new ArrayList<Cell>();
        ListaIndicesCCCErroneos = new ArrayList<Integer>();
        
        ListaDNIRepetidos = new ArrayList<Cell>();
        ListaDNIPosiciones = new ArrayList<Integer>();
        
        ListaEmail = new ArrayList<String>();
        
        ListaFecha = new ArrayList<Calendar>();
        ListaProrrateo = new ArrayList<Cell>();
        
        //abrimos el excel en el constructor
        try {
            file = new FileInputStream(new File(RutaExcel));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            workbook = new XSSFWorkbook(file);
        } catch (IOException ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
        }
        hoja = workbook.getSheetAt(HojaExcel);
        
        obtenerDatos();
    }

    public void corregirDNI() throws IOException, InvalidFormatException{

        for(int j = 0; j < ListaDNI.size(); j++){
            String resultado = encontrarErroresDNI(ListaDNI.get(j)); //le pasamos la celda y nos devuelve el DNI corregido
        
            if(!resultado.equals("")){ // es decir, si esta mal el DNI del excel
                ListaDNI.get(j).setCellValue(resultado); //actualizamos la lista
            }
        }

        int i = 0;
        for(Row fila : hoja){//recorremos las columna y extraemos todas las celdas
            if(i != 0 && ListaDNI.get(i-1) != null){ //evitamos poner en las celdas las posiiones de la lista en nulo

                if(fila.getCell(COLUMNA_DNI) != null || i != 0){ //si la celda esta a nulo no la tocamos
                    fila.getCell(COLUMNA_DNI).setCellValue(ListaDNI.get(i-1).toString()); //actualizamos el valor de las celdas
                }
            }
            i++;
        }

    }
    
    public void completarIBAN() throws FileNotFoundException, IOException{
   
        for(int i = 0; i < getListaCuentas().size(); i++){ //corregimos los ccc y completamos el IBAN
            String digitosDeControlCorrectos = calcularCCC(getListaCuentas().get(i));

            if(!digitosDeControlCorrectos.equals(ListaCuentas.get(i).toString().substring(8,10))){ //si el código de control calculado no es igual al código anterior lo guardamos
                ListaCCCErroneos.add(getListaCuentas().get(i));
                ListaIndicesCCCErroneos.add(i);
            }
            
            getListaCuentas().get(i).setCellValue(getListaCuentas().get(i).toString().substring(0, 8) + digitosDeControlCorrectos + getListaCuentas().get(i).toString().substring(10, 20));         
            //aniadimos el CCC corregido a una lista de IBAN pero sin aniadir el pais aun
            getListaIBAN().add(getListaCuentas().get(i).toString().substring(0, 8) + digitosDeControlCorrectos + getListaCuentas().get(i).toString().substring(10, 20));
        }

        for(int i = 0; i < getListaIBAN().size(); i++){ //calculamos todo los IBAN
            String IBAN = calcularIBAN(getListaIBAN().get(i), ListaPais.get(i));
            getListaIBAN().set(i, IBAN); //sustituimos la valores por los valores de IBAN calculados
        }
        
        int i = 0;
        for(Row fila : hoja){ //actualizamos las celdas      
            if(i != 0){
                fila.getCell(COLUMNA_CODIGO_CUENTA).setCellValue(getListaCuentas().get(i-1).toString()); //actualizamos el valor de las celdas
                //ya que las celdas de IBAN estan vacias incialmente, tenemos que crear unas nuevas en las filas y columnas adecuadas
                Row filaIBAN = hoja.getRow(i); //crearmos la fila
                Cell nuevaCeldaIBAN = filaIBAN.createCell(COLUMNA_IBAN); //y creamos la celda en la fila de antes y la columna donde esta el IBAN
                nuevaCeldaIBAN.setCellValue(getListaIBAN().get(i-1)); //y ponemos el valor
            }
            i++;
        }
    }
    
    public void completarEmail(){
        int i = 0;
        for(Row fila : hoja) {
            if(i != 0){
                StringBuffer email = new StringBuffer(); //creamos un string buffer para ir aniadiendo letras al email

                Cell celdaNombre = fila.getCell(COLUMNA_NOMBRE);
                Cell celdaApellido1 = fila.getCell(COLUMNA_APELLIDO1);
                Cell celdaApellido2 = fila.getCell(COLUMNA_APELLIDO2);
                Cell celdaEmpresa = fila.getCell(COLUMNA_EMPRESA);
                Cell celdaEmail = fila.getCell(COLUMNA_EMAIL);

                if(celdaEmail == null || celdaEmail.toString().equals("")){ //si no tiene email, lo generamos
                    
                    email.append(celdaApellido1.toString().charAt(0));

                    if(celdaApellido2 != null && !celdaApellido2.toString().equals("") /*|| celdaApellido2.toString().equals("Apellido2")*/){ //si apellido2 no esta vacio tambien lo metemos
                       email.append(celdaApellido2.toString().charAt(0));
                    }

                    email.append(celdaNombre.toString().charAt(0));
                    email.append(calcularNumeroRepeticion(email.toString()));
                    email.append("@");
                    email.append(celdaEmpresa);
                    email.append(".es");

                    //tenemos que crear la celda si no da null pointer
                    Row filaEmail = hoja.getRow(i);
                    Cell nuevaCeldaIBAN = filaEmail.createCell(COLUMNA_EMAIL);
                    nuevaCeldaIBAN.setCellValue(email.toString());
                    ListaEmail.add(email.toString());
                }else{//si hay un email lo añadimos a la lista directamente
                    ListaEmail.add(celdaEmail.toString());
                }
            }
            i++;
        }
    
    }
    
    private String encontrarErroresDNI(Cell DNI){

        if(DNI != null){
            boolean extranjero = false;
            boolean X = false,Y = false,Z = false;
            int numeroDNI;
            char letra = DNI.toString().charAt(8);
            if(DNI.toString().substring(0,1).equals("X") || DNI.toString().substring(0,1).equals("Y") || DNI.toString().substring(0,1).equals("Z")){ //si es un DNI extranjero
                extranjero = true;
                String DNIConvertido = "";
                switch (DNI.toString().substring(0,1)) { //quitamos las x,y,z para convertirlo a número
                    case "X":
                        X = true;
                        DNIConvertido = "0" + DNI.toString().substring(1,8);
                        break;
                    case "Y":
                        Y = true;
                        DNIConvertido = "1" + DNI.toString().substring(1,8);
                        break;
                    case "Z":
                        Z = true;
                        DNIConvertido = "2" + DNI.toString().substring(1,8);
                        break;
                }

                numeroDNI = Integer.parseInt(DNIConvertido);

            }else{
                numeroDNI = Integer.parseInt(DNI.toString().substring(0,8));
            }
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE"; 
            int restoDNI = numeroDNI % 23;
            char letraCorrecta = letras.charAt(restoDNI); //hallamos la letra correcta

            if(letraCorrecta != letra){
                if(extranjero){
                    if(X){
                       return "X" + DNI.toString().substring(1,8) + letraCorrecta; 
                    }
                    if(Y){
                       return "Y" + DNI.toString().substring(1,8) + letraCorrecta; 
                    }
                    if(Z){
                       return "Z" + DNI.toString().substring(1,8) + letraCorrecta; 
                    } 
                }else{
                    return DNI.toString().substring(0,8) + letraCorrecta;
                }
            }else{
                return "";
            }
        }
    return "";
    }
    
    private String calcularCCC(Cell codigoCuenta){
        String EO = "00" + codigoCuenta.toString().substring(0,8);
        String NN = codigoCuenta.toString().substring(10, 20);
        
        int primerDigitoControlCuenta = 0;
        int segundoDigitoControlCuenta = 0;
       
        int[] modulos = {1,2,4,8,5,10,9,7,3,6};
        for(int i = 0; i < NN.length(); i++){
            primerDigitoControlCuenta = primerDigitoControlCuenta + Character.getNumericValue(EO.charAt(i)) * modulos[i];
            segundoDigitoControlCuenta = segundoDigitoControlCuenta + Character.getNumericValue(NN.charAt(i)) * modulos[i];
        }
       
        primerDigitoControlCuenta = 11 - (primerDigitoControlCuenta % 11);
        segundoDigitoControlCuenta = 11 -(segundoDigitoControlCuenta % 11);

        if(primerDigitoControlCuenta == 10){
            primerDigitoControlCuenta = 1;
        }
        
        if(primerDigitoControlCuenta == 11){
            primerDigitoControlCuenta = 0;
        }
        
        if(segundoDigitoControlCuenta == 10){
            segundoDigitoControlCuenta = 1;
        }
        
        if(segundoDigitoControlCuenta == 11){
            segundoDigitoControlCuenta = 0;
        }
                
        return Integer.toString(primerDigitoControlCuenta) + Integer.toString(segundoDigitoControlCuenta);
    }
    
    private String calcularIBAN(String codigoCuenta, Cell pais){
        char[] letras = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        String primerNumeroLetra = "";
        String segundoNumeroLetra = "";
        for(int i = 0; i < letras.length; i++){
            if(pais.toString().charAt(0) == letras[i]){
                primerNumeroLetra = Integer.toString(i+10);
            }
            if(pais.toString().charAt(1) == letras[i]){
                segundoNumeroLetra = Integer.toString(i+10);
            }
        }
        
        String IBAN = codigoCuenta + primerNumeroLetra + segundoNumeroLetra + "00";
        BigInteger digitosControl = new BigInteger(IBAN); //cremos un big integer debido a que el numero de cuenta es muy largo
        digitosControl = BigInteger.valueOf(98).subtract(digitosControl.mod(BigInteger.valueOf(97))); //resto 98 en big integer a el modulo 97 de la cuenta anterior en big integer
        String digitosControlCuenta = Arrays.toString(digitosControl.toByteArray()); //convertimos el numero de cuenta a string otra vez, genera un string con corchetes

        if(digitosControl.intValue() < 10){
            digitosControlCuenta = digitosControlCuenta.substring(0,1) + "0" + digitosControlCuenta.substring(1);  
        }
        
        return pais.toString() + digitosControlCuenta.substring(1,3) + codigoCuenta; //el subString para evitar que ponga corchetes debido a la conversion a un array de una posición
    }
    
    private String calcularNumeroRepeticion(String emailSinNumero){
        int contadorRepeticiones = 0;
        for(Row fila : hoja) {
            Cell celdaEmail = fila.getCell(COLUMNA_EMAIL);
            
            if(celdaEmail != null /*|| !celdaEmail.toString().equals("") || !celdaEmail.toString().equals("Email")*/){ //si la celda no esta vacia comprobamos si se repite
                if(emailSinNumero.equals(celdaEmail.toString().split("[0-9]")[0])){ //contamos las veces que esta repetido el email
                    contadorRepeticiones++;
                }
            }
        }
        
        if(contadorRepeticiones < 10){ //si hay menos de 10 repeticiones lo devolvemos con un 0 delante
            return 0 + Integer.toString(contadorRepeticiones);
        }
        
        return Integer.toString(contadorRepeticiones);
    }
    
    public void cerrarExcel() throws FileNotFoundException, IOException{
        FileOutputStream output_file = new FileOutputStream(new File(RutaExcel)); //sobrescribimos el excel
        workbook.write(output_file);
        output_file.close(); //cerramos todo
        workbook.close();
        file.close();
    }
    
    public void DNIRepetidos(){
        //comprobamos si hay DNI repetidos
        ArrayList<Cell> ListaAuxiliar = new ArrayList<>(); 
        boolean repetido;
        for (int i = 0; i < ListaDNI.size(); i++) {
            repetido = false;    
            for(int j = 0; j < ListaAuxiliar.size(); j++){ 
                if(ListaDNI.get(i) != null && ListaAuxiliar.get(j) != null && ListaDNI.get(i).toString().equals(ListaAuxiliar.get(j).toString())) {
                    repetido = true;
                }
            }
            
            if (!repetido) {
                ListaAuxiliar.add(ListaDNI.get(i)); 
            }else{
                ListaDNIRepetidos.add(ListaDNI.get(i));
                ListaDNIPosiciones.add(i); //almacenamos las posiciones de los DNI repetidos para obtener facilmente los demás datos
            } 
        }
    }
    
    private void obtenerDatos() throws ParseException{
        for(Row fila : hoja) { //recorremos todas las columnas
            Cell celdaDNI = fila.getCell(COLUMNA_DNI);
            Cell celdaCodigoCuenta = fila.getCell(COLUMNA_CODIGO_CUENTA);
            Cell celdaPais = fila.getCell(COLUMNA_PAIS);
            Cell celdaCategoria = fila.getCell(COLUMNA_CATEGORIA);
            Cell celdaNombre = fila.getCell(COLUMNA_NOMBRE);
            Cell celdaApellido1 = fila.getCell(COLUMNA_APELLIDO1);
            Cell celdaApellido2 = fila.getCell(COLUMNA_APELLIDO2);
            Cell celdaEmpresa = fila.getCell(COLUMNA_EMPRESA);
            Cell celdaProrrateo = fila.getCell(COLUMNA_PRORRATEO);
            Cell celdaCIF = fila.getCell(COLUMNA_CIF);

            if(!fila.getCell(COLUMNA_FECHA).toString().equals("FechaAltaEmpresa")){
                Date fecha = fila.getCell(COLUMNA_FECHA).getDateCellValue();
     
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("DD/MM/YYYY", Locale.ENGLISH);
                cal.setTime(fecha);
                ListaFecha.add(cal);
            }
            
            

            if(celdaDNI == null || celdaDNI.toString().equals("")){ //si la celda esta vacia aniadimos un null
                Cell celdaVacia = null;
                ListaDNI.add(celdaVacia);
            }else if(!celdaDNI.toString().equals("NIF/NIE")){ //para evitar que incluya NIF/NIE en la lista
                ListaDNI.add(celdaDNI); //aniadimos a una lista de DNI
            }
            
            if(celdaCodigoCuenta != null && !celdaCodigoCuenta.toString().equals("CodigoCuenta")){
                ListaCuentas.add(celdaCodigoCuenta);
            }
            
            if(!celdaPais.toString().equals("Pais Origen Cuenta Bancaria")){
                ListaPais.add(celdaPais);
            }
            
            if(celdaNombre == null || celdaNombre.toString().equals("")){ 
                Cell celdaVacia = null;
                ListaNombre.add(celdaVacia);
            }else if(!celdaNombre.toString().equals("Nombre")){ 
                ListaNombre.add(celdaNombre); 
            }
            
            if(celdaApellido1 == null || celdaApellido1.toString().equals("")){ 
                Cell celdaVacia = null;
                ListaApellido1.add(celdaVacia);
            }else if(!celdaApellido1.toString().equals("Apellido1")){ 
                ListaApellido1.add(celdaApellido1); 
            }
            
            if(celdaApellido2 == null || celdaApellido2.toString().equals("")){ 
                Cell celdaVacia = null;
                ListaApellido2.add(celdaVacia);
            }else if(!celdaApellido2.toString().equals("Apellido2")){ 
                ListaApellido2.add(celdaApellido2); 
            }
            
            if(celdaCategoria == null || celdaCategoria.toString().equals("")){ 
                Cell celdaVacia = null;
                ListaCategoria.add(celdaVacia);
            }else if(!celdaCategoria.toString().equals("Categoria")){ 
                ListaCategoria.add(celdaCategoria); 
            }
            
            if(celdaEmpresa == null || celdaEmpresa.toString().equals("")){ 
                Cell celdaVacia = null;
                ListaEmpresa.add(celdaVacia);
            }else if(!celdaEmpresa.toString().equals("Nombre empresa")){ 
                ListaEmpresa.add(celdaEmpresa); 
            }
            
            if(celdaProrrateo == null || celdaProrrateo.toString().equals("")){ 
                Cell celdaVacia = null;
                ListaProrrateo.add(celdaVacia);
            }else if(!celdaProrrateo.toString().equals("ProrrataExtra")){ 
                ListaProrrateo.add(celdaProrrateo); 
            }
            
            if(celdaCIF == null || celdaCIF.toString().equals("")){ 
                Cell celdaVacia = null;
                ListaCIF.add(celdaVacia);
            }else if(!celdaCIF.toString().equals("Cif empresa")){ 
                ListaCIF.add(celdaCIF); 
            }
                    
        } 
    }
    
    //getters para llamar desde la otra clase
    public List<Cell> getListaCCCErroneos() {
        return ListaCCCErroneos;
    }

    public List<Integer> getListaIndicesCCCErroneos() {
        return ListaIndicesCCCErroneos;
    }

    public List<Cell> getListaCuentas() {
        return ListaCuentas;
    }

    public List<String> getListaIBAN() {
        return ListaIBAN;
    }

    public List<String> getListaEmail() {
        return ListaEmail;
    }
    public List<Cell> getListaNombre() {
        return ListaNombre;
    }

    public List<Cell> getListaApellido1() {
        return ListaApellido1;
    }

    public List<Cell> getListaApellido2() {
        return ListaApellido2;
    }

    public List<Cell> getListaEmpresa() {
        return ListaEmpresa;
    }

    public List<Cell> getListaCategoria() {
        return ListaCategoria;
    }
    
    public List<Cell> getListaDNI(){
        return ListaDNI;
    }
    
    public List<Cell> getListaDNIRepetidos(){
        return ListaDNIRepetidos;
    }
    
    public List<Integer> getListaDNIPosiciones(){
        return ListaDNIPosiciones;
    }
    
    //para la clase NominaTrabajador
    public List<Calendar> getListaFecha(){
        return ListaFecha;
    }
    
    public List<Cell> getListaProrrateo(){
        return ListaProrrateo;
    }
    
    public List<Cell> getListaCIF(){
        return ListaCIF;
    }

}
