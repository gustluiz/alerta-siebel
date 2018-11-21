package com.alertaSiebel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AlertaSiebel {

	HashMap<String, Defeito> listaDefeitos = new HashMap<String, Defeito>();
	private int countChecksNoChange;
	
	public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException {
		
		AlertaSiebel osu = new AlertaSiebel();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		 
		osu.carregaListaDefeitoDoArquivo();		
		while(true) {
			osu.verificaDefeitosWeb();
			
			System.out.println("Verificado em:" + dateFormat.format(new Date(System.currentTimeMillis())) );
			Thread.sleep(60*1000);
			osu.gravaListaDefeitosNoArquivo();
			
		}
	}
	
	public void verificaDefeitosWeb() throws MalformedURLException, IOException {

		HashMap<String, Defeito> novalistaDefeitos = new HashMap<String, Defeito>();
		
		Notify note = new Notify("elainebrasil@protonmail.com, elainebrasil@gmail.com, gustavo.l.ferreira@gmail.com");
		//Notify note = new Notify("gustavo.l.ferreira@gmail.com");

		this.countChecksNoChange = 0;
		
		String url = "http://104.41.15.137:9000/mobile/detalhesfab/SIEBEL";
		// Obtem os dados da pagina web
		Document doc = Jsoup.connect(url).get();

		// Percorre os elementos Tabela <table></table>
		Elements tables = doc.select("table");
		for ( int t = 0; t < tables.size(); t++ ) {
			Element table = tables.get(t);
			Elements rows = table.select("tr");

			// Guarda os atributo id de tabela
			String statusTableId = table.attr("id");			
			// Percorre as linha da tabela
			for (int i = 1; i < rows.size(); i++) {
				Element row = rows.get(i);
				Elements cols = row.select("td");
				
				
				// Guarda as coluna da tabela no objeto Defeito
				Defeito d = new Defeito(cols.get(0).text(), cols.get(1).text(), cols.get(2).text(), 
						cols.get(3).text(), cols.get(4).text(), cols.get(5).text(), cols.get(6).text(), statusTableId);
				if ( d.getDefeitoId().contains( ( "PRJ32468" ) ) ) {									
					novalistaDefeitos.put(d.getDefeitoId(), d);										
				} 
			}
		}
		
		// Se encontrar alteracao na lsita de defeitos com a nova lista obitda pela web, envia por email
		StringBuffer msg = new StringBuffer();
		if( existeAlteracaoListaDefeitos(novalistaDefeitos) ) {
		
			Collection<Defeito> ld = this.listaDefeitos.values();
			for (Defeito d : ld) {				
				msg.append(d.toString());		
				msg.append("\r\n=====================================================================\r\n");								
			}
			System.out.println(msg);
			note.enviaEmail(msg.toString());		
			this.countChecksNoChange = 0;
		} else {
			this.countChecksNoChange++;
			if( this.countChecksNoChange == 30) {
				this.countChecksNoChange = 0;
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");				
				msg.append("Nenhuma alteração desde " + dateFormat.format( new Date( System.currentTimeMillis() ) ) + "\r\n");	
				msg.append("\r\n=====================================================================\r\n");
				Collection<Defeito> ld = this.listaDefeitos.values();
				for (Defeito d : ld) {				
					msg.append(d.toString());		
					msg.append("\r\n=====================================================================\r\n");								
				}				
				System.out.println(msg);
				//note.enviaEmail(msg.toString());
			}
		}
		
		
	}
	
	private boolean existeAlteracaoListaDefeitos( HashMap<String, Defeito> novaListaDefeitos) {
		
		boolean ret = false;
					
		
		// Percorre lista de defeito 
		for (Defeito defeitoAtual : this.listaDefeitos.values() ) {
			// Verifica se existe na nova lista e atualiza se forem diferentes
			if(  novaListaDefeitos.containsKey( (  defeitoAtual.getDefeitoId()  ) ) ) {
			
				Defeito novoDefeito = novaListaDefeitos.get( defeitoAtual.getDefeitoId() );

				if( !novoDefeito.equals(  defeitoAtual ) ) {
				
					this.listaDefeitos.replace( novoDefeito.getDefeitoId(), novoDefeito);
					ret = true;
				} 				
			} else {
				ret = true;
				this.listaDefeitos.remove( defeitoAtual.getDefeitoId() );
			}
		}
		
		// Percorre a lista nova de defeitos
		for (Defeito novoDefeito : novaListaDefeitos.values() ) {
			if( !this.listaDefeitos.containsKey( novoDefeito.getDefeitoId() ) ) {
				this.listaDefeitos.put( novoDefeito.getDefeitoId() , novoDefeito );
				ret = true;
			}  
		}

				
		
		return ret;
	}

	public void gravaListaDefeitosNoArquivo() {		
		File arqDefeitos = new File("listaDefeitos.bin");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(arqDefeitos);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.listaDefeitos);
			fos.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	

	@SuppressWarnings("unchecked")
	public void carregaListaDefeitoDoArquivo() {
		File arqDefeitos = new File("listaDefeitos.bin");
		FileInputStream fis;
		try {
			fis = new FileInputStream(arqDefeitos);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			this.listaDefeitos = (HashMap<String, Defeito>)ois.readObject();
			fis.close();
		} catch (FileNotFoundException e) {			
			System.out.println("Arquivo de listaDefeitos não existe");
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
}
