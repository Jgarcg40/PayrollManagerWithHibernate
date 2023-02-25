package nominas;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import java.io.FileNotFoundException;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.text.DecimalFormat;
import java.util.Calendar;

public class PDFNominas {
    
    int indiceTrabajador;
    NominaTrabajador datosTrabajador;
    Excel excel;
    
    public PDFNominas(int indiceTrabajador, NominaTrabajador datosTrabajador, Excel excel){
        this.indiceTrabajador = indiceTrabajador;
        this.datosTrabajador = datosTrabajador;
        this.excel = excel;
    }
    
    public void crearPDF() throws FileNotFoundException{
        
        PdfWriter pdfWriter = new PdfWriter("../resources/" + excel.getListaDNI().get(indiceTrabajador) + excel.getListaNombre().get(indiceTrabajador) + excel.getListaApellido1().get(indiceTrabajador) + excel.getListaApellido2().get(indiceTrabajador) + datosTrabajador.getFecha().substring(3, 7) + datosTrabajador.getFecha().substring(0, 2) +".pdf");
        PdfDocument pdfDoc = new PdfDocument(pdfWriter);
        Document doc = new Document(pdfDoc, PageSize.LETTER);
        //si es un mes con una extra creamos otro pdf
        
            DecimalFormat df = new DecimalFormat("##.##");
            doc.add(new Paragraph("EMPRESA:"));
            doc.add(new Paragraph("Nombre:" + excel.getListaEmpresa().get(indiceTrabajador) + "."));
            doc.add(new Paragraph("CIF:" + excel.getListaCIF().get(indiceTrabajador) + "."));
            doc.add(new Paragraph("TRABAJADOR:"));
            doc.add(new Paragraph("Categoría:" + excel.getListaCategoria().get(indiceTrabajador) + "."));
            doc.add(new Paragraph("Bruto anual:"+ df.format(datosTrabajador.getBrutoAnual())  + "."));
            doc.add(new Paragraph("Fecha de alta:"  + (excel.getListaFecha().get(indiceTrabajador).get(Calendar.DAY_OF_MONTH)) + "/" + (excel.getListaFecha().get(indiceTrabajador).get(Calendar.MONTH) + 1) + "/" + (excel.getListaFecha().get(indiceTrabajador).get(Calendar.YEAR))+ "."));
            doc.add(new Paragraph("IBAN:" + excel.getListaIBAN().get(indiceTrabajador) + "."));
            doc.add(new Paragraph("Nombre:" + excel.getListaNombre().get(indiceTrabajador) + "."));
            doc.add(new Paragraph("Apellido:" + excel.getListaApellido1().get(indiceTrabajador) + " "));
            if(excel.getListaApellido2().get(indiceTrabajador) != null){
                doc.add(new Paragraph(excel.getListaApellido2().get(indiceTrabajador) + "."));
            }else{
                doc.add(new Paragraph("."));
            }
            doc.add(new Paragraph("DNI:" + excel.getListaDNI().get(indiceTrabajador)));
            if(!datosTrabajador.prorrateo(indiceTrabajador) && (Integer.parseInt(datosTrabajador.getFecha().substring(0, 2)) == 6 || Integer.parseInt(datosTrabajador.getFecha().substring(0, 2)) == 12)){
                doc.add(new Paragraph("FECHA:" + datosTrabajador.getFecha() + ". Este mes incluye una extra."));
            }else{
                doc.add(new Paragraph("FECHA:" + datosTrabajador.getFecha() + "."));
            }
            doc.add(new Paragraph("IMPORTES A PERCIBIR EL TRABAJADOR:"));
            doc.add(new Paragraph("Salario base mes:" + df.format(datosTrabajador.getSalarioBaseMes()) + "."));
            if(datosTrabajador.prorrateo(indiceTrabajador)){
                doc.add(new Paragraph("Prorrateo mes:"+ df.format((((datosTrabajador.getBrutoAnual()/14)/12))*2)));
            }else{
                doc.add(new Paragraph("Prorrateo mes:0,00."));
            }
            doc.add(new Paragraph("Complemento mes:" + df.format(datosTrabajador.getComplementoMes()) + "."));
            doc.add(new Paragraph("Antigüedad mes:" + df.format(datosTrabajador.getAntiguedadMes()) + "."));
            doc.add(new Paragraph("DESCUENTOS TRABAJADOR(porcentaje, valor, importe):"));
            if(!datosTrabajador.prorrateo(indiceTrabajador)){
            doc.add(new Paragraph("Seguridad social:"+ df.format(datosTrabajador.getCuotaObreraTrabajador()) + "%/" + df.format(datosTrabajador.getBrutoMensual() + ((datosTrabajador.getBrutoMensual()/12) *2)) + "/" + df.format(datosTrabajador.getSSTrabajador()) + "."));
            doc.add(new Paragraph("Desempleo:" + df.format(datosTrabajador.getCuotaDesempleoTrabajador()) + "%/" + df.format(datosTrabajador.getBrutoMensual() + ((datosTrabajador.getBrutoMensual()/12) *2)) + "/" + df.format(datosTrabajador.getDesempleoTrabajador()) + "."));
            doc.add(new Paragraph("Cuota de formación:"+ df.format(datosTrabajador.getCuotaFormacionTrabajador()) + "%/" + df.format(datosTrabajador.getBrutoMensual() + ((datosTrabajador.getBrutoMensual()/12) *2)) + "/"  + df.format(datosTrabajador.getFormacionTrabajador()) + "."));
            doc.add(new Paragraph("IRPF:" + df.format(datosTrabajador.getCuotaIRPF()) + "%/" + df.format(datosTrabajador.getBrutoMensual()) + "/" +  df.format(datosTrabajador.getIRPFTrabajador()) + ".")); //imprimir sobre 14 meses la base no sobre 12
            }else{
            doc.add(new Paragraph("Seguridad social:"+ df.format(datosTrabajador.getCuotaObreraTrabajador()) + "%/" + df.format(datosTrabajador.getBrutoMensual()) + "/" + df.format(datosTrabajador.getSSTrabajador()) + "."));
            doc.add(new Paragraph("Desempleo:" + df.format(datosTrabajador.getCuotaDesempleoTrabajador()) + "%/" + df.format(datosTrabajador.getBrutoMensual()) + "/" + df.format(datosTrabajador.getDesempleoTrabajador()) + "."));
            doc.add(new Paragraph("Cuota de formación:"+ df.format(datosTrabajador.getCuotaFormacionTrabajador()) + "%/" + df.format(datosTrabajador.getBrutoMensual()) + "/"  + df.format(datosTrabajador.getFormacionTrabajador()) + "."));
            doc.add(new Paragraph("IRPF:" + df.format(datosTrabajador.getCuotaIRPF()) + "%/" + df.format(datosTrabajador.getBrutoMensual()) + "/" +  df.format(datosTrabajador.getIRPFTrabajador()) + ".")); //imprimir sobre 14 meses la base no sobre 12
            }
            doc.add(new Paragraph("TOTAL DEVENGOS Y DEDUCCIONES:"));
            if(datosTrabajador.prorrateo(indiceTrabajador)){
            doc.add(new Paragraph("Devengos:" + df.format(datosTrabajador.getBrutoMensual())));//brutoAnual/12
            }else{
                doc.add(new Paragraph("Devengos:" + df.format(datosTrabajador.getBrutoMensual())));//brutoAnual/14
             }
            doc.add(new Paragraph("Deducciones:" + df.format(datosTrabajador.getSSTrabajador() + datosTrabajador.getDesempleoTrabajador() + datosTrabajador.getFormacionTrabajador() + datosTrabajador.getIRPFTrabajador())));
            doc.add(new Paragraph("Líquido a percibir:" + df.format(datosTrabajador.getLiquidoAPercibir())));
            doc.add(new Paragraph("PAGOS EMPRESARIO(porcentaje, importe):"));
            if(!datosTrabajador.prorrateo(indiceTrabajador)){
            doc.add(new Paragraph("Base sobre la que se produce:" + df.format(datosTrabajador.getBrutoMensual() + ((datosTrabajador.getBrutoMensual()/12) *2)) + "."));
                }else{
            doc.add(new Paragraph("Base sobre la que se produce:" + df.format(datosTrabajador.getBrutoMensual()) + "."));
                }
            doc.add(new Paragraph("Seguridad social:"+ df.format(datosTrabajador.getCuotaContigenciasEmpresario()) + "%/" + df.format(datosTrabajador.getSSEmpresario()) + "."));
            doc.add(new Paragraph("Desempleo:" + df.format(datosTrabajador.getCuotaDesempleoEmpresario()) + "%/" + df.format(datosTrabajador.getDesempleoEmpresario()) + "."));
            doc.add(new Paragraph("Cuota de formación:" + df.format(datosTrabajador.getCuotaFormacionEmpresario()) + "%/" + df.format(datosTrabajador.getFormacionEmpresario()) + "."));
            doc.add(new Paragraph("Accidentes de trabajo:" + df.format(datosTrabajador.getCuotaAccidentesEmpresario()) + "%/" + df.format(datosTrabajador.getAccidentesTrabajo()) + "."));
            doc.add(new Paragraph("FOGASA:" + df.format(datosTrabajador.getCuotaFogasaEmpresario()) + "%/" + df.format(datosTrabajador.getFOGASAEmpresario()) + "."));
            doc.add(new Paragraph("Total:" + df.format(datosTrabajador.getSSEmpresario() + datosTrabajador.getDesempleoEmpresario() + datosTrabajador.getFormacionEmpresario() + datosTrabajador.getAccidentesTrabajo() + datosTrabajador.getFOGASAEmpresario())));
            doc.add(new Paragraph("COSTE TOTAL DEL TRABAJADOR PARA EL EMPRESARIO:" +  df.format(datosTrabajador.getBrutoMensual() + datosTrabajador.getSSEmpresario() + datosTrabajador.getDesempleoEmpresario() + datosTrabajador.getFormacionEmpresario() + datosTrabajador.getAccidentesTrabajo() + datosTrabajador.getFOGASAEmpresario())));
            doc.close();
            
        if((datosTrabajador.getFecha().charAt(1) == '6' || datosTrabajador.getFecha().substring(0, 2).equals("12")) && !datosTrabajador.prorrateo(indiceTrabajador)){//ojo mirar si es hasta 2 o hasta 3
            PdfWriter pdfWriterExtra = new PdfWriter("../resources/" + excel.getListaDNI().get(indiceTrabajador) + excel.getListaNombre().get(indiceTrabajador) + excel.getListaApellido1().get(indiceTrabajador) + excel.getListaApellido2().get(indiceTrabajador) + datosTrabajador.getFecha().substring(3, 7) + datosTrabajador.getFecha().substring(0, 2) + "EXTRA.pdf");
            PdfDocument pdfDocExtra = new PdfDocument(pdfWriterExtra);
            Document docExtra = new Document(pdfDocExtra, PageSize.LETTER);
        
            docExtra.add(new Paragraph("EMPRESA:"));
            docExtra.add(new Paragraph("Nombre:" + excel.getListaEmpresa().get(indiceTrabajador) + "."));
            docExtra.add(new Paragraph("CIF:" + excel.getListaCIF().get(indiceTrabajador) + "."));
            docExtra.add(new Paragraph("TRABAJADOR:"));
            docExtra.add(new Paragraph("Categoría:" + excel.getListaCategoria().get(indiceTrabajador) + "."));
            docExtra.add(new Paragraph("Bruto anual:"+ df.format(datosTrabajador.getBrutoAnual())  + "."));
            docExtra.add(new Paragraph("Fecha de alta:"  + (excel.getListaFecha().get(indiceTrabajador).get(Calendar.DAY_OF_MONTH)) + "/" + (excel.getListaFecha().get(indiceTrabajador).get(Calendar.MONTH) + 1) + "/" + (excel.getListaFecha().get(indiceTrabajador).get(Calendar.YEAR))+ "."));
            docExtra.add(new Paragraph("IBAN:" + excel.getListaIBAN().get(indiceTrabajador) + "."));
            docExtra.add(new Paragraph("Nombre:" + excel.getListaNombre().get(indiceTrabajador) + "."));
            docExtra.add(new Paragraph("Apellido:" + excel.getListaApellido1().get(indiceTrabajador) + " "));
            if(excel.getListaApellido2().get(indiceTrabajador) != null){
                docExtra.add(new Paragraph(excel.getListaApellido2().get(indiceTrabajador) + "."));
            }else{
                docExtra.add(new Paragraph("."));
            }
            docExtra.add(new Paragraph("DNI:" + excel.getListaDNI().get(indiceTrabajador)));
            if((Integer.parseInt(datosTrabajador.getFecha().substring(0, 2)) == 6 || Integer.parseInt(datosTrabajador.getFecha().substring(0, 2)) == 12)){
                docExtra.add(new Paragraph("FECHA:" + datosTrabajador.getFecha() + ". Este mes incluye una extra."));
            }
            docExtra.add(new Paragraph("IMPORTES A PERCIBIR EL TRABAJADOR:"));
            docExtra.add(new Paragraph("Salario base mes:" + df.format(datosTrabajador.getSalarioBaseMes()) + "."));
            docExtra.add(new Paragraph("Prorrateo mes:0,00."));
            docExtra.add(new Paragraph("Complemento mes:" + df.format(datosTrabajador.getComplementoMes()) + "."));
            docExtra.add(new Paragraph("Antigüedad mes:" + df.format(datosTrabajador.getAntiguedadMes()) + "."));
            docExtra.add(new Paragraph("DESCUENTOS TRABAJADOR(porcentaje, valor, importe):"));
            docExtra.add(new Paragraph("Seguridad social:"+ df.format(datosTrabajador.getCuotaObreraTrabajador()) + "%/" + "0.0" + "/" + "0.0" + "."));
            docExtra.add(new Paragraph("Desempleo:" + df.format(datosTrabajador.getCuotaDesempleoTrabajador()) + "%/" + "0.0" + "0.0" + "."));
            docExtra.add(new Paragraph("Cuota de formación:"+ df.format(datosTrabajador.getCuotaFormacionTrabajador()) + "%/" + "0.0" + "0.0" + "."));
            docExtra.add(new Paragraph("IRPF:" + df.format(datosTrabajador.getCuotaIRPF()) + "%/" + df.format(datosTrabajador.getBrutoMensual()) + "/" +  df.format(datosTrabajador.getIRPFTrabajador()) + ".")); //imprimir sobre 14 meses la base no sobre 12
            docExtra.add(new Paragraph("TOTAL DEVENGOS Y DEDUCCIONES:"));
            docExtra.add(new Paragraph("Devengos:" + df.format(datosTrabajador.getBrutoMensual())));//brutoAnual/14
            docExtra.add(new Paragraph("Deducciones:" + df.format(datosTrabajador.getSSTrabajador() + datosTrabajador.getDesempleoTrabajador() + datosTrabajador.getFormacionTrabajador() + datosTrabajador.getIRPFTrabajador())));
            docExtra.add(new Paragraph("Líquido a percibir:" + df.format(datosTrabajador.getBrutoMensual() - datosTrabajador.getIRPFTrabajador())));
            docExtra.add(new Paragraph("PAGOS EMPRESARIO(porcentaje, importe):"));
            docExtra.add(new Paragraph("Base sobre la que se produce:" + "0.0" + "."));  
            docExtra.add(new Paragraph("Seguridad social:"+ df.format(datosTrabajador.getCuotaContigenciasEmpresario()) + "%/" + "0.0" + "."));
            docExtra.add(new Paragraph("Desempleo:" + df.format(datosTrabajador.getCuotaDesempleoEmpresario()) + "%/" + "0.0" + "."));
            docExtra.add(new Paragraph("Cuota de formación:" + df.format(datosTrabajador.getCuotaFormacionEmpresario()) + "%/" + "0.0" + "."));
            docExtra.add(new Paragraph("Accidentes de trabajo:" + df.format(datosTrabajador.getCuotaAccidentesEmpresario()) + "%/" + "0.0" + "."));
            docExtra.add(new Paragraph("FOGASA:" + df.format(datosTrabajador.getCuotaFogasaEmpresario()) + "%/" + "0.0" + "."));
            docExtra.add(new Paragraph("Total:" + "0.0"));
            docExtra.add(new Paragraph("COSTE TOTAL DEL TRABAJADOR PARA EL EMPRESARIO:" +  df.format(datosTrabajador.getBrutoMensual())));
            docExtra.close();
        }
        
    }    
}
    


