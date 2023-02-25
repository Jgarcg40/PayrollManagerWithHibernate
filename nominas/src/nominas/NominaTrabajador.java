package nominas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class NominaTrabajador {
     
    private Excel excel;
    private String fecha;
    
    private final String RutaExcel;
    private final int HojaExcel;
    
    private int COLUMNA_CATEGORIA = 0;
    private int COLUMNA_SALARIO_BASE = 1;
    private int COLUMNA_COMPLEMENTOS = 2;
    private int COLUMNA_TRIENIOS = 3;
    private int COLUMNA_BRUTO_ANUAL_IRPF = 5;
    private int COLUMNA_RETENCION = 6;
  
    private float cuotaIRPF;
    private float brutoMensual;
    private int mesEntradaTrabajador;
    private int aniosAntiguedad;
    private float brutoAnual;
    private float salarioBaseMes;
    private float complementoMes;
    private float antiguedadMes;
    private int trienios;
    private float cuotaObreraTrabajador;
    private float cuotaDesempleoTrabajador;
    private float cuotaFormacionTrabajador;
    private float cuotaContigenciasEmpresario;
    private float cuotaFogasaEmpresario;
    private float cuotaDesempleoEmpresario;
    private float cuotaFormacionEmpresario;
    private float cuotaAccidentesEmpresario;
    private float SSTrabajador;
    private float formacionTrabajador;
    private float desempleoTrabajador;
    private float IRPFTrabajador;
    private float liquidoAPercibir;
    private float SSEmpresario;
    private float FOGASAEmpresario;
    private float desempleoEmpresario;
    private float formacionEmpresario;
    private float accidentesTrabajo;
    
    private List<Cell> listaCategoria;
    private List<Cell> listaSalarioBase;
    private List<Cell> listaComplementos;
    private List<Integer> listaIndicesTrabajadores;
    private List<Cell> listaTrienios;
    private List<Cell> listaBrutoAnualIRPF;
    private List<Cell> listaRetencion;
    
    private List<DatosNominas> datosNominas; //almacena los datos de las nominas de todos los trabjadores de eses mes
    
    private Sheet hoja;
    private XSSFWorkbook workbook;
    private FileInputStream file;
    
    public NominaTrabajador(String RutaExcel, int HojaExcel, Excel excel){
        this.excel = excel;
        this.RutaExcel = RutaExcel;
        this.HojaExcel = HojaExcel;
        
        listaIndicesTrabajadores = new ArrayList<Integer>();
        listaCategoria = new ArrayList<Cell>();
        listaSalarioBase = new ArrayList<Cell>();
        listaComplementos = new ArrayList<Cell>();
        listaTrienios = new ArrayList<Cell>();
        listaBrutoAnualIRPF = new ArrayList<Cell>();
        listaRetencion = new ArrayList<Cell>();
        datosNominas = new ArrayList<DatosNominas>();
        
        cuotaIRPF = 0;
        brutoMensual = 0;
        mesEntradaTrabajador = 0;
        aniosAntiguedad = 0;
        brutoAnual = 0;
        salarioBaseMes = 0;
        complementoMes = 0;
        antiguedadMes = 0;
        trienios = 0;
        cuotaObreraTrabajador = 0;
        cuotaDesempleoTrabajador = 0;
        cuotaFormacionTrabajador = 0;
        cuotaContigenciasEmpresario = 0;
        cuotaFogasaEmpresario = 0;
        cuotaDesempleoEmpresario = 0;
        cuotaFormacionEmpresario = 0;
        cuotaAccidentesEmpresario = 0;
        SSTrabajador = 0;
        formacionTrabajador = 0;
        desempleoTrabajador = 0;
        IRPFTrabajador = 0;
        liquidoAPercibir = 0;
        SSEmpresario = 0;
        FOGASAEmpresario = 0;
        desempleoEmpresario = 0;
        formacionEmpresario = 0;
        accidentesTrabajo = 0;
        
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
    }
    
    private void leerFecha(){
        Scanner teclado = new Scanner(System.in);
        fecha = teclado.nextLine();
        teclado.close();
    }
    
    //obtenemos los trabajadores que hayan sido contratado esa fecha
    public void obtenerNominas(){
        leerFecha();
        obtenerDatos();
        for(int i = 0; i < excel.getListaFecha().size(); i++){
            boolean correcto = false;
            //Calendar.MONTH imprime los meses empezando por cero en vez de por uno, por lo que hay que sumar uno al mes obtenido!!!
            if(excel.getListaFecha().get(i).get(Calendar.YEAR) < Integer.parseInt(fecha.substring(3, 7))){
                correcto = true;
            }else if(excel.getListaFecha().get(i).get(Calendar.YEAR) == Integer.parseInt(fecha.substring(3, 7))){
                if(excel.getListaFecha().get(i).get(Calendar.MONTH)+1 <= Integer.parseInt(fecha.substring(0, 2))){
                    correcto = true;
                }
            }
            
            if(correcto){
                listaIndicesTrabajadores.add(i);
            }
            
        }
        
        for(int i = 0; i < listaIndicesTrabajadores.size(); i++){
            brutoAnual(listaIndicesTrabajadores.get(i));
            importesAPercibirTrabajador(listaIndicesTrabajadores.get(i));
            descuentosTrabajador(listaIndicesTrabajadores.get(i));
            pagosEmpresario(listaIndicesTrabajadores.get(i));
            imprimirResultado(listaIndicesTrabajadores.get(i));
            
            PDFNominas pdf = new PDFNominas(listaIndicesTrabajadores.get(i),this,excel);
            try {
                pdf.crearPDF();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(NominaTrabajador.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            almacenarDatosNominas(listaIndicesTrabajadores.get(i));
        }
    }
    
    
    
    private void brutoAnual(int indiceTrabajador){
        brutoAnual = 0;
        boolean cambioDeTrienio = false;
        aniosAntiguedad = Integer.parseInt(fecha.substring(3, 7)) - excel.getListaFecha().get(indiceTrabajador).get(Calendar.YEAR);
        
        int contador = Integer.parseInt((fecha.substring(3, 7)));

        while( contador >  excel.getListaFecha().get(indiceTrabajador).get(Calendar.YEAR) && aniosAntiguedad > 2){
            contador = contador - 3;
        }
        
        if(contador == excel.getListaFecha().get(indiceTrabajador).get(Calendar.YEAR)){ //si es igual es que en el año que se mete por la entrada hay cambio de trienio para el trabajador
            
            cambioDeTrienio = true;
        }
        
        if(excel.getListaFecha().get(indiceTrabajador).get(Calendar.MONTH)+1 > Integer.parseInt(fecha.substring(0, 2)) && trienios != 0 && cambioDeTrienio){ //si el mes es menor, no ha llegado a cumplir ese año
            aniosAntiguedad--;
        }

        trienios = aniosAntiguedad / 3;
        
        mesEntradaTrabajador =  excel.getListaFecha().get(indiceTrabajador).get(Calendar.MONTH) + 1; //almacenamos el mes que habría cambio de trienio
        
        if(prorrateo(indiceTrabajador)){
            if(aniosAntiguedad == 0){ //cuando no tiene antigüedad
                brutoAnual = (((Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString())) + Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString()))/12)*(12-mesEntradaTrabajador);
            }
            if(aniosAntiguedad > 0 && aniosAntiguedad < 3){ //cuando tiene menos de un trienio se cuenta todo el año
               brutoAnual = (((Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString())) + Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString()))/12)*12; 
            }
        }else{
            if(aniosAntiguedad == 0){
                float brutoExtraJunio = 0;
                float brutoExtraDiciembre = 0;
                
                brutoAnual = ((Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) + Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString()))/14)*(12-mesEntradaTrabajador+1);//12-mes-1
               
                if(mesEntradaTrabajador <= 6){
                    brutoExtraJunio = ((Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) + Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString()))/14)/(6/(mesEntradaTrabajador-2)); //la extra se cuenta desde el 1 de diciembre del año anterior hasta el 31 de mayo y desde el 1 de junio hasta el 31 de noviembre
                    brutoExtraDiciembre = ((Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) + Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString()))/14); // se cuenta junio y diciembre
                }
               
                if(mesEntradaTrabajador > 6){
                    brutoExtraDiciembre = ((Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) + Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString()))/14)/(6/mesEntradaTrabajador-5); //no se si esta bien
                }
                brutoAnual = brutoAnual + /*brutoAnualJunio + brutoAnualDiciembre*/ + brutoExtraJunio + brutoExtraDiciembre; 
            }
            if(aniosAntiguedad > 0 && aniosAntiguedad < 3){ //cuando tiene menos de un trienio se cuenta todo el año
               brutoAnual = (((Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString())) + Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString()))/14)*14; 
            }
        }

        if(trienios > 0){ //cuando tiene antigüedad
            if(cambioDeTrienio){
                if(trienios > 1){
                    brutoAnual = Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) + Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) + (mesEntradaTrabajador * Float.parseFloat(listaTrienios.get(trienios-2).toString()))/*trienios -2 porque aun no ha cambiado de trienio*/ + ((12 -mesEntradaTrabajador) * Float.parseFloat(listaTrienios.get(trienios-1).toString()) + Float.parseFloat(listaTrienios.get(trienios-1).toString()) + Float.parseFloat(listaTrienios.get(trienios-1).toString())); //tambien le sumamos las extra de junio y diciembre que suma con el trieno correspondiente a
                }else{
                     brutoAnual = Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) + Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) + Float.parseFloat(listaTrienios.get(trienios-1).toString());
                }
            }else{
                brutoAnual = Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) + Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) + (14 *Float.parseFloat(listaTrienios.get(trienios-1).toString())); //triemios porque la lista empieza en 0 y los trienios en 1
            }
        }
    }
    
    private void importesAPercibirTrabajador(int indiceTrabajador){
        salarioBaseMes = Float.parseFloat(listaSalarioBase.get(obtenerIndiceCategoria(indiceTrabajador)).toString()) / 14;
        complementoMes = Float.parseFloat(listaComplementos.get(obtenerIndiceCategoria(indiceTrabajador)).toString())/14;
        if(trienios > 0){
            antiguedadMes = Float.parseFloat(listaTrienios.get(trienios-1).toString());
        }else{
            antiguedadMes = 0;
        }
    }
    
    private void descuentosTrabajador(int indiceTrabajador){
        brutoMensual = 0;
        cuotaIRPF = 0;
        IRPFTrabajador = 0;
  
        if(aniosAntiguedad == 0 && !prorrateo(indiceTrabajador)){ //si aniosAntiguedad = 0 el bruto mensual se calcula para los mese que ha trabajado ese año
            brutoMensual = salarioBaseMes + complementoMes;
        }else if(aniosAntiguedad == 0 && prorrateo(indiceTrabajador)){
            brutoMensual = salarioBaseMes + complementoMes + (((brutoAnual/14)/12)*2);//MIRAR 
        }else if(aniosAntiguedad > 0 && !prorrateo(indiceTrabajador)){
            brutoMensual = salarioBaseMes + complementoMes + antiguedadMes;
        }else if(aniosAntiguedad > 0 && prorrateo(indiceTrabajador)){
            brutoMensual = brutoAnual/12;
        }
        
        if(!prorrateo(indiceTrabajador)){
            //para calcula el bruto mensual hay que tener en cuenta los mese trabajados de ese año, no los del todo año si no ha trabajado todo ese año
            SSTrabajador = (brutoMensual + ((brutoMensual / 12)*2)) * (cuotaObreraTrabajador / 100);
            desempleoTrabajador = (brutoMensual + ((brutoMensual / 12)*2)) * (cuotaDesempleoTrabajador / 100);
            formacionTrabajador = (brutoMensual + ((brutoMensual / 12)*2)) * (cuotaFormacionTrabajador / 100);

            for(int i = listaBrutoAnualIRPF.size()-1; i >= 0; i--){
                if(brutoAnual > Float.parseFloat(listaBrutoAnualIRPF.get(i).toString())){
                    if(i==48){
                       cuotaIRPF = Float.parseFloat(listaRetencion.get(i).toString());
                       IRPFTrabajador = brutoMensual * (Float.parseFloat(listaRetencion.get(i).toString())/100);
                    }else{
                    cuotaIRPF = Float.parseFloat(listaRetencion.get(i+1).toString());
                    IRPFTrabajador = brutoMensual * (Float.parseFloat(listaRetencion.get(i+1).toString())/100);
                    }
                    break;
                }
            }
           
            liquidoAPercibir = brutoMensual - SSTrabajador - desempleoTrabajador - IRPFTrabajador - formacionTrabajador;
         
        }else{

            SSTrabajador = brutoMensual * (cuotaObreraTrabajador / 100);
            desempleoTrabajador = brutoMensual * (cuotaDesempleoTrabajador / 100);
            formacionTrabajador = brutoMensual * (cuotaFormacionTrabajador / 100);

            for(int i = listaBrutoAnualIRPF.size()-1; i >= 0; i--){
                if(brutoAnual > Float.parseFloat(listaBrutoAnualIRPF.get(i).toString())){
                    if(i==48){
                        cuotaIRPF = Float.parseFloat(listaRetencion.get(i).toString());
                        IRPFTrabajador = brutoMensual * (Float.parseFloat(listaRetencion.get(i).toString())/100);
                    }else{
                        cuotaIRPF = Float.parseFloat(listaRetencion.get(i+1).toString());
                        IRPFTrabajador = brutoMensual * (Float.parseFloat(listaRetencion.get(i+1).toString())/100);  
                    }
                    break;
                }
            }

            liquidoAPercibir = brutoMensual - SSTrabajador - desempleoTrabajador - IRPFTrabajador - formacionTrabajador;
        }
    }
    
    private void pagosEmpresario(int indiceTrabajador){
        if(!prorrateo(indiceTrabajador)){

            SSEmpresario = (brutoMensual + ((brutoMensual / 12)*2)) * (cuotaContigenciasEmpresario/ 100);
            desempleoEmpresario = (brutoMensual + ((brutoMensual / 12)*2)) * (cuotaDesempleoEmpresario / 100);
            formacionEmpresario = (brutoMensual + ((brutoMensual / 12)*2)) * (cuotaFormacionEmpresario / 100);
            FOGASAEmpresario = (brutoMensual + ((brutoMensual / 12)*2)) * (cuotaFogasaEmpresario/ 100);
            accidentesTrabajo = (brutoMensual + ((brutoMensual / 12)*2)) * (cuotaAccidentesEmpresario/ 100);

        }else{

            SSEmpresario = brutoMensual * (cuotaContigenciasEmpresario / 100);
            desempleoEmpresario = brutoMensual * (cuotaDesempleoEmpresario / 100);
            formacionEmpresario = brutoMensual * (cuotaFormacionEmpresario / 100);
            FOGASAEmpresario = brutoMensual * (cuotaFogasaEmpresario/ 100);
            accidentesTrabajo = brutoMensual * (cuotaAccidentesEmpresario/ 100);
            
        }
    }
    
    private void imprimirResultado(int indiceTrabajador){
        DecimalFormat df = new DecimalFormat("##.##");
        System.out.println("*********************************************************************************************************************************");
        System.out.println("EMPRESA:");
        System.out.println("Nombre:" + excel.getListaEmpresa().get(indiceTrabajador).toString() + ".");
        System.out.println("CIF:" + excel.getListaCIF().get(indiceTrabajador).toString() + ".");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("TRABAJADOR:");
        System.out.println("Categoría:" + excel.getListaCategoria().get(indiceTrabajador).toString() + ".");
        System.out.println("Bruto anual:"+ df.format(brutoAnual)  + ".");
        System.out.println("Fecha de alta:"  + (excel.getListaFecha().get(indiceTrabajador).get(Calendar.DAY_OF_MONTH)) + "/" + (excel.getListaFecha().get(indiceTrabajador).get(Calendar.MONTH) + 1) + "/" + (excel.getListaFecha().get(indiceTrabajador).get(Calendar.YEAR))+ "."); //+1 en el mes porque, los meses en la clase calendar empiezan en cero
        System.out.println("IBAN:" + excel.getListaIBAN().get(indiceTrabajador) + ".");
        System.out.println("Nombre:" + excel.getListaNombre().get(indiceTrabajador).toString() + ".");
        System.out.print("Apellido:" + excel.getListaApellido1().get(indiceTrabajador).toString() + " ");
        
        if(excel.getListaApellido2().get(indiceTrabajador) != null){
            System.out.println(excel.getListaApellido2().get(indiceTrabajador) + ".");
        }else{
            System.out.println(".");
        }
        
        System.out.println("DNI:" + excel.getListaDNI().get(indiceTrabajador));
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
        if(!prorrateo(indiceTrabajador) && (Integer.parseInt(fecha.substring(0, 2)) == 6 || Integer.parseInt(fecha.substring(0, 2)) == 12)){
            System.out.println("FECHA:" + fecha + ". Este mes incluye una extra.");
        }else{
            System.out.println("FECHA:" + fecha + ".");
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("IMPORTES A PERCIBIR EL TRABAJADOR:");
        System.out.println("Salario base mes:" + df.format(salarioBaseMes) + ".");
        if(prorrateo(indiceTrabajador)){
            System.out.println("Prorrateo mes:"+ df.format((((brutoAnual/14)/12))*2));
        }else{
            System.out.println("Prorrateo mes:0,00.");
        }
        System.out.println("Complemento mes:" + df.format(complementoMes) + ".");
        System.out.println("Antigüedad mes:" + df.format(antiguedadMes) + ".");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("DESCUENTOS TRABAJADOR(porcentaje, valor, importe):");
        if(!prorrateo(indiceTrabajador)){
            System.out.println("Seguridad social:"+ df.format(cuotaObreraTrabajador) + "%/" + df.format(brutoMensual + ((brutoMensual/12) *2)) + "/" + df.format(SSTrabajador) + ".");
            System.out.println("Desempleo:" + df.format(cuotaDesempleoTrabajador) + "%/" + df.format(brutoMensual + ((brutoMensual/12) *2)) + "/" + df.format(desempleoTrabajador) + ".");
            System.out.println("Cuota de formación:"+ df.format(cuotaFormacionTrabajador) + "%/" + df.format(brutoMensual + ((brutoMensual/12) *2)) + "/"  + df.format(formacionTrabajador) + ".");
            System.out.println("IRPF:" + df.format(cuotaIRPF) + "%/" + df.format(brutoMensual) + "/" +  df.format(IRPFTrabajador) + "."); //imprimir sobre 14 meses la base no sobre 12
        }else{
            System.out.println("Seguridad social:"+ df.format(cuotaObreraTrabajador) + "%/" + df.format(brutoMensual) + "/" + df.format(SSTrabajador) + ".");
            System.out.println("Desempleo:" + df.format(cuotaDesempleoTrabajador) + "%/" + df.format(brutoMensual) + "/" + df.format(desempleoTrabajador) + ".");
            System.out.println("Cuota de formación:"+ df.format(cuotaFormacionTrabajador) + "%/" + df.format(brutoMensual) + "/"  + df.format(formacionTrabajador) + ".");
            System.out.println("IRPF:" + df.format(cuotaIRPF) + "%/" + df.format(brutoMensual) + "/" +  df.format(IRPFTrabajador) + "."); //imprimir sobre 14 meses la base no sobre 12
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("TOTAL DEVENGOS Y DEDUCCIONES:");
        if(prorrateo(indiceTrabajador)){
            System.out.println("Devengos:" + df.format(brutoMensual));//brutoAnual/12
        }else{
            System.out.println("Devengos:" + df.format(brutoMensual));//brutoAnual/14
        }
        System.out.println("Deducciones:" + df.format(SSTrabajador + desempleoTrabajador + formacionTrabajador + IRPFTrabajador));
        System.out.println("Líquido a percibir:" + df.format(liquidoAPercibir));
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("PAGOS EMPRESARIO(porcentaje, importe):");
        if(!prorrateo(indiceTrabajador)){
            System.out.println("Base sobre la que se produce:" + df.format(brutoMensual + ((brutoMensual/12) *2)) + ".");
        }else{
            System.out.println("Base sobre la que se produce:" + df.format(brutoMensual) + ".");
        }
        System.out.println("Seguridad social:"+ df.format(cuotaContigenciasEmpresario) + "%/" + df.format(SSEmpresario) + ".");
        System.out.println("Desempleo:" + df.format(cuotaDesempleoEmpresario) + "%/" + df.format(desempleoEmpresario) + ".");
        System.out.println("Cuota de formación:" + df.format(cuotaFormacionEmpresario) + "%/" + df.format(formacionEmpresario) + ".");
        System.out.println("Accidentes de trabajo:" + df.format(cuotaAccidentesEmpresario) + "%/" + df.format(accidentesTrabajo) + ".");
        System.out.println("FOGASA:" + df.format(cuotaFogasaEmpresario) + "%/" + df.format(FOGASAEmpresario) + ".");
        System.out.println("Total:" + df.format(SSEmpresario + desempleoEmpresario + formacionEmpresario + accidentesTrabajo + FOGASAEmpresario));
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("COSTE TOTAL DEL TRABAJADOR PARA EL EMPRESARIO:" +  df.format(brutoMensual + SSEmpresario + desempleoEmpresario + formacionEmpresario + accidentesTrabajo + FOGASAEmpresario));
    }
    
    private void obtenerDatos(){
       
        for(Row fila : hoja) {
            if(fila.getCell(COLUMNA_CATEGORIA) == null){
                break;
            } 
            if(!fila.getCell(COLUMNA_CATEGORIA).toString().equals("Categoria") ){
             Cell celdaCategoria = fila.getCell(COLUMNA_CATEGORIA);
             listaCategoria.add(celdaCategoria);
            }
         
            if(!fila.getCell(COLUMNA_SALARIO_BASE).toString().equals("Salario Base") ){
             Cell celdaSalarioBase = fila.getCell(COLUMNA_SALARIO_BASE);
             listaSalarioBase.add(celdaSalarioBase);
            }
            
            if(!fila.getCell(COLUMNA_COMPLEMENTOS).toString().equals("Complementos") ){
             Cell celdaComplementos = fila.getCell(COLUMNA_COMPLEMENTOS);
             listaComplementos.add(celdaComplementos);
            }
        }
        
        for(Row fila : hoja) {
            if(fila.getCell(COLUMNA_TRIENIOS) != null && !fila.getCell(COLUMNA_TRIENIOS).toString().equals("Importe bruto") ){
             Cell celdaTrienios = fila.getCell(COLUMNA_TRIENIOS);
             listaTrienios.add(celdaTrienios);
            }
        }
        
        for(Row fila : hoja) {
            if(fila.getCell(COLUMNA_CATEGORIA) != null){
                switch(fila.getCell(COLUMNA_CATEGORIA).toString()){
                    case "Cuota obrera general TRABAJADOR":
                        cuotaObreraTrabajador = Float.parseFloat(fila.getCell(COLUMNA_SALARIO_BASE).toString());
                        break;
                    case "Cuota desempleo TRABAJADOR":
                        cuotaDesempleoTrabajador = Float.parseFloat(fila.getCell(COLUMNA_SALARIO_BASE).toString());
                        break;
                    case "Cuota formación TRABAJADOR":
                        cuotaFormacionTrabajador = Float.parseFloat(fila.getCell(COLUMNA_SALARIO_BASE).toString());
                        break;
                    case "Contingencias comunes EMPRESARIO":
                        cuotaContigenciasEmpresario = Float.parseFloat(fila.getCell(COLUMNA_SALARIO_BASE).toString());
                        break;
                    case "Fogasa EMPRESARIO":
                        cuotaFogasaEmpresario = Float.parseFloat(fila.getCell(COLUMNA_SALARIO_BASE).toString());
                        break;
                    case "Desempleo EMPRESARIO":
                        cuotaDesempleoEmpresario = Float.parseFloat(fila.getCell(COLUMNA_SALARIO_BASE).toString());
                        break;
                    case "Formacion EMPRESARIO":
                        cuotaFormacionEmpresario = Float.parseFloat(fila.getCell(COLUMNA_SALARIO_BASE).toString());
                        break;
                    case "Accidentes trabajo EMPRESARIO":
                        cuotaAccidentesEmpresario = Float.parseFloat(fila.getCell(COLUMNA_SALARIO_BASE).toString());
                        break;

                }
            }
            if(fila.getCell(COLUMNA_TRIENIOS) != null){
                if(!fila.getCell(COLUMNA_TRIENIOS).toString().equals("Importe bruto")){
                    Cell celdaTrienios = fila.getCell(COLUMNA_TRIENIOS);
                    listaTrienios.add(celdaTrienios);
                }
            }
        }
        
        for(Row fila : hoja) {
            if(!fila.getCell(COLUMNA_BRUTO_ANUAL_IRPF).toString().equals("Bruto anual")){
             Cell celdaBrutoAnual = fila.getCell(COLUMNA_BRUTO_ANUAL_IRPF);
             listaBrutoAnualIRPF.add(celdaBrutoAnual);
            }
            
            if(!fila.getCell(COLUMNA_RETENCION).toString().equals("Retención")){
             Cell celdaRetencion = fila.getCell(COLUMNA_RETENCION);
             listaRetencion.add(celdaRetencion);
            }
        }
    }
    
    public boolean prorrateo(int indiceTrabajador){
        boolean prorrateoTrabajador = false;
        if(excel.getListaProrrateo().get(indiceTrabajador).toString().equals("SI")){
            prorrateoTrabajador = true;
        }
        return prorrateoTrabajador;
    }
    
    private int obtenerIndiceCategoria(int indiceTrabajador){
        for(int i = 0; i < listaCategoria.size(); i++){
            if(listaCategoria.get(i).toString().equals(excel.getListaCategoria().get(indiceTrabajador).toString())){
                 return i;       
            }
        }
        return 0;
    }
    
    private void almacenarDatosNominas(int indiceTrabajador){
        datosNominas.add(new DatosNominas()); //añadimos una nuevaNomina
        datosNominas.get(datosNominas.size()-1).setMes(Integer.parseInt(fecha.substring(0, 2))); //cojemos la última para modificarla y añadirle todos los atributos
        datosNominas.get(datosNominas.size()-1).setAnio(Integer.parseInt(fecha.substring(3, 7)));
        datosNominas.get(datosNominas.size()-1).setNumeroTrienios(trienios);
        datosNominas.get(datosNominas.size()-1).setImporteTrienios(antiguedadMes);
        datosNominas.get(datosNominas.size()-1).setImporteSalarioMes(salarioBaseMes);
        datosNominas.get(datosNominas.size()-1).setImporteComplementoMes(complementoMes);
        if(prorrateo(indiceTrabajador)){ //si hay prorrateo lo calculamos
            datosNominas.get(datosNominas.size()-1).setValorProrrateo(((((brutoAnual/14)/12))*2));
        }else{//si no, ponemos el valor a cero
            datosNominas.get(datosNominas.size()-1).setValorProrrateo(0);
        }
        datosNominas.get(datosNominas.size()-1).setBrutoAnual(brutoAnual);
        datosNominas.get(datosNominas.size()-1).setIrpf(cuotaIRPF);
        datosNominas.get(datosNominas.size()-1).setImporteIrpf(IRPFTrabajador);
        if(!prorrateo(indiceTrabajador)){
             datosNominas.get(datosNominas.size()-1).setBaseEmpresario((brutoMensual + ((brutoMensual/12) *2)));
        }else{
            datosNominas.get(datosNominas.size()-1).setBaseEmpresario(brutoMensual);
        }
        datosNominas.get(datosNominas.size()-1).setSeguridadSocialEmpresario(cuotaContigenciasEmpresario);
        datosNominas.get(datosNominas.size()-1).setImporteSeguridadSocialEmpresario(SSEmpresario);
        datosNominas.get(datosNominas.size()-1).setDesempleoEmpresario(cuotaDesempleoEmpresario);
        datosNominas.get(datosNominas.size()-1).setImporteDesempleoEmpresario(desempleoEmpresario);
        datosNominas.get(datosNominas.size()-1).setFormacionEmpresario(cuotaFormacionEmpresario);
        datosNominas.get(datosNominas.size()-1).setImporteFormacionEmpresario(formacionEmpresario);
        datosNominas.get(datosNominas.size()-1).setAccidentesTrabajoEmpresario(cuotaAccidentesEmpresario);
        datosNominas.get(datosNominas.size()-1).setImporteAccidentesTrabajoEmpresario(accidentesTrabajo);
        datosNominas.get(datosNominas.size()-1).setFogasaempresario(cuotaFogasaEmpresario);
        datosNominas.get(datosNominas.size()-1).setImporteFogasaempresario(FOGASAEmpresario);
        datosNominas.get(datosNominas.size()-1).setSeguridadSocialTrabajador(cuotaObreraTrabajador);
        datosNominas.get(datosNominas.size()-1).setImporteSeguridadSocialTrabajador(SSTrabajador);
        datosNominas.get(datosNominas.size()-1).setDesempleoTrabajador(cuotaDesempleoTrabajador);
        datosNominas.get(datosNominas.size()-1).setImporteDesempleoTrabajador(desempleoTrabajador);
        datosNominas.get(datosNominas.size()-1).setFormacionTrabajador(cuotaFormacionTrabajador);
        datosNominas.get(datosNominas.size()-1).setImporteFormacionTrabajador(formacionTrabajador);
        datosNominas.get(datosNominas.size()-1).setBrutoNomina(brutoMensual);
        datosNominas.get(datosNominas.size()-1).setLiquidoNomina(liquidoAPercibir);
        datosNominas.get(datosNominas.size()-1).setCosteTotalEmpresario(brutoMensual + SSEmpresario + desempleoEmpresario + formacionEmpresario + accidentesTrabajo + FOGASAEmpresario);

    }
    
    public void cerrarExcel() throws FileNotFoundException, IOException{
        workbook.close();
        file.close();
    }
    
    //getters
    
    public float getCuotaIRPF() {
        return cuotaIRPF;
    }
    public float getBrutoMensual() {
        return brutoMensual;
    }
    public int getMesEntradaTrabajador() {
        return mesEntradaTrabajador;
    }
    public int getAniosAntiguedad() {
        return aniosAntiguedad;
    }
    public float getBrutoAnual() {
        return brutoAnual;
    }
    public float getSalarioBaseMes() {
        return salarioBaseMes;
    }
    public float getComplementoMes() {
        return complementoMes;
    }
    public float getAntiguedadMes() {
        return antiguedadMes;
    }
    public int getTrienios() {
        return trienios;
    }
    public float getCuotaObreraTrabajador() {
        return cuotaObreraTrabajador;
    }
     public float getCuotaDesempleoTrabajador() {
        return cuotaDesempleoTrabajador;
    }
    public float getCuotaFormacionTrabajador() {
        return cuotaFormacionTrabajador;
    }
    public float getCuotaContigenciasEmpresario() {
        return cuotaContigenciasEmpresario;
    }
     public float getCuotaFogasaEmpresario() {
        return cuotaFogasaEmpresario;
    }
    public float getCuotaDesempleoEmpresario() {
        return cuotaDesempleoEmpresario;
    }
    public float getCuotaFormacionEmpresario() {
        return cuotaFormacionEmpresario;
    }
    public float getCuotaAccidentesEmpresario() {
        return cuotaAccidentesEmpresario;
    }
    public float getSSTrabajador() {
        return SSTrabajador;
    }
    public float getFormacionTrabajador() {
        return formacionTrabajador;
    }
    public float getDesempleoTrabajador() {
        return desempleoTrabajador;
    }
    public float getIRPFTrabajador() {
        return IRPFTrabajador;
    }
    public float getLiquidoAPercibir() {
        return liquidoAPercibir;
    }
    public float getSSEmpresario() {
        return SSEmpresario;
    }
    public float getFOGASAEmpresario() {
        return FOGASAEmpresario;
    }
    public float getDesempleoEmpresario() {
        return desempleoEmpresario;
    }
    public float getFormacionEmpresario() {
        return formacionEmpresario;
    }
    public float getAccidentesTrabajo() {
        return accidentesTrabajo;
    }
    
    public List<Cell> getListaSalariosBase(){
        return listaSalarioBase;
    }
    
    public List<Cell> getListaCategorias(){
        return listaCategoria;
    }
    
    public List<Cell> getListaComplementos(){
        return listaComplementos;
    }
            
    public String getFecha(){
        return fecha;
    }
    public List<Integer> getListaIndicesTrabajadores(){
        return listaIndicesTrabajadores;
    }
    
    public List<DatosNominas> getListaDatosNominas(){
        return datosNominas;
    }
}