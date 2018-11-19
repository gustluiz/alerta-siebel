package com.alertaSiebel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Defeito implements Comparable<Defeito> , Serializable {	

	private static final long serialVersionUID = 1L;
	private String defeitoId;
	private String severidade;
	private String sla;
	private String dataInicio;
	private String dataAlteracao;
	private String projeto;
	private String descricao;
	private String status;
	
	private static Map<String, String> defeitoMap = new HashMap<String, String>();
	
	static {
		defeitoMap.put("inprogresstable", "IN PROGRESS");
		defeitoMap.put("pendentretesttable", "PENDENT (RETEST)");
		defeitoMap.put("pendentprogresstable", "PENDENT (PROGRESS)");
		defeitoMap.put("onretesttable", "ON RETEST");
		defeitoMap.put("rejectedtable", "REJECTED");
		defeitoMap.put("reopentable", "REOPEN");
		
	}
	
	public Defeito(String defeitoId, String severidade, 
			String sla, String dataInicio, String dataAlteracao,
			String projeto, String descricao, String statusId) {
		this.defeitoId = defeitoId;
		this.severidade = severidade;
		this.sla = sla;
		this.dataInicio = dataInicio;
		this.dataAlteracao = dataAlteracao;
		this.projeto = projeto;
		this.descricao = descricao;
		if( Defeito.defeitoMap.containsKey( ( statusId ) ) ){
			this.status = Defeito.defeitoMap.get( statusId );
		} else {
			this.status = statusId;
		}
	}
	
	@Override
	public String toString() {
		return "Defeito:" + this.defeitoId  + "\r\n" + 
		"Severidade:" + this.severidade  + "\r\n" +
		"Sla:" + this.sla  + "\r\n" +
		"DataInicio:" + this.dataInicio + "\r\n" +
		"DataAlteracao:" + this.dataAlteracao + "\r\n" +
		"Projeto:" + this.projeto + "\r\n" +
		"Descricao:" + this.descricao + "\r\n" +
		"Status:" + this.status; 
	}

	public String getDefeitoId() {
		return this.defeitoId;
	}

	@Override
	public int compareTo(Defeito d) {
		return ( this.status.compareTo( d.status ) );
	}
	
	@Override 	
	public boolean equals( Object o) {
		Defeito d = (Defeito)o;
		return this.status.equals( d.status );
		
	}
	
	

}
