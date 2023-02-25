package nominas;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import modelo.Categorias;
import modelo.Empresas;
import modelo.Nomina;
import modelo.Trabajadorbbdd;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class BaseDeDatos {
    
    private Excel excel;
    private NominaTrabajador datosTrabajador;
    
    private List<Trabajadorbbdd> listaTrabajadorbbdd;
    private List<Categorias> listaCategoriasbbdd;
    private List<Empresas> listaEmpresasbbdd;
    private List<Nomina> listaNominabbdd;
    
    SessionFactory sf;
    Session session;
    
    public BaseDeDatos(Excel excel, NominaTrabajador datosTrabajador){
        this.excel = excel;
        this.datosTrabajador = datosTrabajador;
                
        //abrimos sesión
        sf = new NewHibernateUtil().getSessionFactory();
        session = sf.openSession();
    }
    
    public void actualizarBaseDeDatos(){
        DecimalFormat df = new DecimalFormat("#.##");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//para que el formato de fecha al compararlo sea el mismo
        for(int i = 0; i < datosTrabajador.getListaIndicesTrabajadores().size(); i++){
            obtenerDatos();//obtenemos los datos de nuevo para que se actualizen las listas y detecte los repetidos
            boolean sinDNI = false; //si el DNI esta en blanco no añadimos ni el trabajador ni la nomina
            if(excel.getListaDNI().get(datosTrabajador.getListaIndicesTrabajadores().get(i)) == null || excel.getListaDNI().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString().equals("")){
                sinDNI = true;
            }
                boolean coincideTrabajador = false;
                if(!sinDNI){
                    for(Trabajadorbbdd trabajador:listaTrabajadorbbdd){
                        if(trabajador.getNombre().equals(excel.getListaNombre().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString()) && trabajador.getNifnie().equals(excel.getListaDNI().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString()) && trabajador.getFechaAlta().toString().equals(format.format(excel.getListaFecha().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).getTime()))){
                            coincideTrabajador = true;
                        }
                    }
                }
                boolean coincideCategoria = false;
                for(Categorias categoria:listaCategoriasbbdd){
                    if(excel.getListaCategoria().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString().equals(categoria.getNombreCategoria())){
                        coincideCategoria = true;
                    }

                }
                boolean coincideCIF = false;
                for(Empresas empresa:listaEmpresasbbdd){
                    if(excel.getListaCIF().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString().equals(empresa.getCif())){
                        coincideCIF = true;
                    }
                }

                boolean coincideNomina = false;
                if(!sinDNI){
                    
                    for(Nomina nomina:listaNominabbdd){  
                        if(Integer.parseInt(format.format(excel.getListaFecha().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).getTime()).substring(5, 7)) < 10 && Integer.toString(nomina.getMes()).equals(format.format(excel.getListaFecha().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).getTime()).substring(6, 7)) && Integer.toString(nomina.getAnio()).equals(format.format(excel.getListaFecha().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).getTime()).substring(0, 4)) && mismoTrabajadorNominaBBDD(datosTrabajador.getListaIndicesTrabajadores().get(i), nomina) && df.format(nomina.getBrutoNomina()).equals(df.format(datosTrabajador.getBrutoMensual())) && df.format(nomina.getLiquidoNomina()).equals(df.format(datosTrabajador.getLiquidoAPercibir()))){
                            coincideNomina = true;
                        }else if(Integer.parseInt(format.format(excel.getListaFecha().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).getTime()).substring(5, 7)) >= 10 && Integer.toString(nomina.getMes()).equals(format.format(excel.getListaFecha().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).getTime()).substring(5, 7)) && Integer.toString(nomina.getAnio()).equals(format.format(excel.getListaFecha().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).getTime()).substring(0, 4)) && mismoTrabajadorNominaBBDD(datosTrabajador.getListaIndicesTrabajadores().get(i), nomina) && df.format(nomina.getBrutoNomina()).equals(df.format(datosTrabajador.getBrutoMensual())) && df.format(nomina.getLiquidoNomina()).equals(df.format(datosTrabajador.getLiquidoAPercibir()))){
                            coincideNomina = true;
                        }
                    }
                }
                Empresas nuevaEmpresa = new Empresas();
                if(!coincideCIF){
                    nuevaEmpresa.setCif(excel.getListaCIF().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString());
                    nuevaEmpresa.setNombre(excel.getListaEmpresa().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString());
                }

                Categorias nuevaCategoria = new Categorias();
                if(!coincideCategoria){
                    nuevaCategoria.setNombreCategoria(excel.getListaCategoria().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString());
                    
                    for(int j = 0; j < datosTrabajador.getListaCategorias().size(); j++){ //recorremo la lista de salarios base
                        if(datosTrabajador.getListaCategorias().get(j).equals(excel.getListaCategoria().get(datosTrabajador.getListaIndicesTrabajadores().get(j)))){ //si la categoria de de la hoja 2 es igual al del la del trabajador(hoja 1), añadimos el salario base
                            nuevaCategoria.setSalarioBaseCategoria(Double.parseDouble(datosTrabajador.getListaSalariosBase().get(j).toString()));
                            nuevaCategoria.setComplementoCategoria(Double.parseDouble(datosTrabajador.getListaComplementos().get(j).toString()));
                        }
                    }
                }

                Trabajadorbbdd nuevoTrabajador = new Trabajadorbbdd();
                if(!sinDNI){
                    if(!coincideTrabajador){
                        nuevoTrabajador.setApellido1(excel.getListaApellido1().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString());
                        if(excel.getListaApellido2().get(datosTrabajador.getListaIndicesTrabajadores().get(i)) == null || excel.getListaApellido2().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString().equals("")){//si el apellido2 esta vacio o nulo, añadimos un cadena vacia
                            nuevoTrabajador.setApellido2("");
                        }else{
                            nuevoTrabajador.setApellido2(excel.getListaApellido2().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString());
                        }

                        nuevoTrabajador.setCodigoCuenta(excel.getListaCuentas().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString());
                        nuevoTrabajador.setEmail(excel.getListaEmail().get(datosTrabajador.getListaIndicesTrabajadores().get(i)));
                        nuevoTrabajador.setIban(excel.getListaIBAN().get(datosTrabajador.getListaIndicesTrabajadores().get(i)));
                        nuevoTrabajador.setNifnie(excel.getListaDNI().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString());
                        nuevoTrabajador.setNombre(excel.getListaNombre().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString());
                        nuevoTrabajador.setFechaAlta(excel.getListaFecha().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).getTime()); //se convierte de Calendar a Date con .getTime()

                        if(!coincideCIF){ //si no coincide el CIF es que se ha creado una nueva Empresa anteriormente
                            nuevoTrabajador.setEmpresas(nuevaEmpresa);
                        }else{//si no tenemos que cojer el objeto de la base de datos
                            for(Empresas empresa:listaEmpresasbbdd){
                                if(empresa.getCif().equals(excel.getListaCIF().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString())){
                                    nuevoTrabajador.setEmpresas(empresa); //añadimos la empresa que nos coincide correctamente
                                }
                            }
                        }

                        if(!coincideCategoria){ //lo mismo con categoría
                            nuevoTrabajador.setCategorias(nuevaCategoria);
                        }else{
                            for(Categorias categoria:listaCategoriasbbdd){
                                if(categoria.getNombreCategoria().equals(excel.getListaCategoria().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString())){
                                    nuevoTrabajador.setCategorias(categoria);
                                }
                            }
                        }
                    }
                }
                
                Nomina nuevaNomina = new Nomina();
                if(!sinDNI){
                    if(!coincideNomina){
                        nuevaNomina.setMes(datosTrabajador.getListaDatosNominas().get(i).getMes());
                        nuevaNomina.setAnio(datosTrabajador.getListaDatosNominas().get(i).getAnio());
                        nuevaNomina.setNumeroTrienios(datosTrabajador.getListaDatosNominas().get(i).getNumeroTrienios());
                        nuevaNomina.setImporteTrienios(datosTrabajador.getListaDatosNominas().get(i).getImporteTrienios());
                        nuevaNomina.setImporteSalarioMes(datosTrabajador.getListaDatosNominas().get(i).getImporteSalarioMes());
                        nuevaNomina.setImporteComplementoMes(datosTrabajador.getListaDatosNominas().get(i).getImporteComplementoMes());
                        nuevaNomina.setValorProrrateo(datosTrabajador.getListaDatosNominas().get(i).getValorProrrateo());
                        nuevaNomina.setBrutoAnual(datosTrabajador.getListaDatosNominas().get(i).getBrutoAnual());
                        nuevaNomina.setIrpf(datosTrabajador.getListaDatosNominas().get(i).getIrpf());
                        nuevaNomina.setImporteIrpf(datosTrabajador.getListaDatosNominas().get(i).getImporteIrpf());
                        nuevaNomina.setBaseEmpresario(datosTrabajador.getListaDatosNominas().get(i).getBaseEmpresario());
                        nuevaNomina.setSeguridadSocialEmpresario(datosTrabajador.getListaDatosNominas().get(i).getSeguridadSocialEmpresario());
                        nuevaNomina.setImporteSeguridadSocialEmpresario(datosTrabajador.getListaDatosNominas().get(i).getImporteSeguridadSocialEmpresario());
                        nuevaNomina.setDesempleoEmpresario(datosTrabajador.getListaDatosNominas().get(i).getDesempleoEmpresario());
                        nuevaNomina.setImporteDesempleoEmpresario(datosTrabajador.getListaDatosNominas().get(i).getImporteDesempleoEmpresario());
                        nuevaNomina.setFormacionEmpresario(datosTrabajador.getListaDatosNominas().get(i).getFormacionEmpresario());
                        nuevaNomina.setImporteFormacionEmpresario(datosTrabajador.getListaDatosNominas().get(i).getImporteFormacionEmpresario());
                        nuevaNomina.setAccidentesTrabajoEmpresario(datosTrabajador.getListaDatosNominas().get(i).getAccidentesTrabajoEmpresario());
                        nuevaNomina.setImporteAccidentesTrabajoEmpresario(datosTrabajador.getListaDatosNominas().get(i).getImporteAccidentesTrabajoEmpresario());
                        nuevaNomina.setFogasaempresario(datosTrabajador.getListaDatosNominas().get(i).getFogasaempresario());
                        nuevaNomina.setImporteFogasaempresario(datosTrabajador.getListaDatosNominas().get(i).getImporteFogasaempresario());
                        nuevaNomina.setSeguridadSocialTrabajador(datosTrabajador.getListaDatosNominas().get(i).getSeguridadSocialTrabajador());
                        nuevaNomina.setImporteSeguridadSocialTrabajador(datosTrabajador.getListaDatosNominas().get(i).getImporteSeguridadSocialTrabajador());
                        nuevaNomina.setDesempleoTrabajador(datosTrabajador.getListaDatosNominas().get(i).getDesempleoTrabajador());
                        nuevaNomina.setImporteDesempleoTrabajador(datosTrabajador.getListaDatosNominas().get(i).getImporteDesempleoTrabajador());
                        nuevaNomina.setFormacionTrabajador(datosTrabajador.getListaDatosNominas().get(i).getFormacionTrabajador());
                        nuevaNomina.setImporteFormacionTrabajador(datosTrabajador.getListaDatosNominas().get(i).getImporteFormacionTrabajador());
                        nuevaNomina.setBrutoNomina(datosTrabajador.getListaDatosNominas().get(i).getBrutoNomina());
                        nuevaNomina.setLiquidoNomina(datosTrabajador.getListaDatosNominas().get(i).getLiquidoNomina());
                        nuevaNomina.setCosteTotalEmpresario(datosTrabajador.getListaDatosNominas().get(i).getCosteTotalEmpresario());

                        if(!coincideTrabajador){
                            nuevaNomina.setTrabajadorbbdd(nuevoTrabajador);
                        }else{
                            for(Trabajadorbbdd trabajador:listaTrabajadorbbdd){
                                if(trabajador.getNombre().equals(excel.getListaNombre().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString()) && trabajador.getNifnie().equals(excel.getListaDNI().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).toString()) && trabajador.getFechaAlta().toString().equals(format.format(excel.getListaFecha().get(datosTrabajador.getListaIndicesTrabajadores().get(i)).getTime()))){
                                    nuevaNomina.setTrabajadorbbdd(trabajador);
                                }
                            }
                        }
                    }
                }
                
                if(!coincideTrabajador || !coincideCategoria || !coincideNomina || !coincideCIF){ //si todo esta en la base de datos no la abrimos
                    session.beginTransaction();//empieza la transaccion
                }
                
                if(!coincideCIF){
                    session.save(nuevaEmpresa);
                }
                
                if(!coincideCategoria){
                    session.save(nuevaCategoria);
                }
                
                if(!coincideTrabajador && !sinDNI){ //solo se actualiza si no existe en la base de datos
                    session.save(nuevoTrabajador);
                }
                
                if(!coincideNomina && !sinDNI){
                    session.save(nuevaNomina);
                }
                
                if(!coincideTrabajador || !coincideCategoria || !coincideNomina || !coincideCIF){
                    session.getTransaction().commit(); //hace el cambio en la base de datos
                }   
        }
    }
    
    private boolean mismoTrabajadorNominaBBDD(int indiceTrabajador, Nomina nomina){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if(nomina.getTrabajadorbbdd().getNombre().equals(excel.getListaNombre().get(datosTrabajador.getListaIndicesTrabajadores().get(indiceTrabajador)).toString()) && nomina.getTrabajadorbbdd().getNifnie().equals(excel.getListaDNI().get(datosTrabajador.getListaIndicesTrabajadores().get(indiceTrabajador)).toString()) && nomina.getTrabajadorbbdd().getFechaAlta().toString().equals(format.format(excel.getListaFecha().get(datosTrabajador.getListaIndicesTrabajadores().get(indiceTrabajador)).getTime()))){
            return true;
        }else{
            return false;
        }
    }
    private void obtenerDatos(){
        listaTrabajadorbbdd = new ArrayList<Trabajadorbbdd>();
        listaCategoriasbbdd = new ArrayList<Categorias>();
        listaEmpresasbbdd = new ArrayList<Empresas>();
        listaNominabbdd = new ArrayList<Nomina>();
        
        Query consulta;
        consulta = session.createQuery("from Trabajadorbbdd");
        //creamos una lista de los objetos devueltos por la consulta
        listaTrabajadorbbdd = consulta.list();
        consulta = session.createQuery("from Categorias");
        listaCategoriasbbdd = consulta.list();
        consulta = session.createQuery("from Empresas");
        listaEmpresasbbdd = consulta.list();
        consulta = session.createQuery("from Nomina");
        listaNominabbdd = consulta.list();
    }
            
    public void cerrarSesion(){
        session.close();
        sf.close(); 
    }
}
