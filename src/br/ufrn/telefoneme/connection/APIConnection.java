package br.ufrn.telefoneme.connection;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import br.ufrn.telefoneme.exception.ConnectionException;
import br.ufrn.telefoneme.exception.IdException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Example of the OAuth client credentials flow using the Apache Oltu OAuth2 client.
 */
public class APIConnection implements AbstractConnection {
    /**
     * URL for requesting OAuth access tokens.
     */
    public final String TOKEN_REQUEST_URL = "https://apitestes.info.ufrn.br/authz-server/oauth/token";

    /**
     * Client ID of your client credential.  Change this to match whatever credential you have created.
     */
    public final String CLIENT_ID = "mobile-services-id";

    /**
     * Client secret of your client credential.  Change this to match whatever credential you have created.
     */
    public final String CLIENT_SECRET = "segredo";

    public final String RESOURCE_URL_TPL = "https://apitestes.info.ufrn.br";

    
    public String getTurmas(String string,String ano, String periodo) throws ConnectionException{
    	return getDados("/ensino-services/services/consulta/turmas/centro",string+"/"+ano+"/"+periodo);
    }
    
    public String getCursos(String nivel) throws ConnectionException{
    	return getDados("/curso-services/services/consulta/curso",nivel);
    }
    
    public String getMatrizCurricular(Integer idCurso) throws ConnectionException,IdException{
    	if(idCurso < 0){
    		throw new IdException("ID menor que zero!");  
    	}
        return getDados("/curso-services/services/consulta/curso/matriz/graduacao",idCurso);	
    }
    
    public String getComponentes(Long idCurriculo) throws ConnectionException,IdException{
    	if(idCurriculo<0){
    		throw new IdException("ID menor que zero!"); 
    	}
    	return getDados("/curso-services/services/consulta/curso/componentes/detalhes",idCurriculo);
    }
    
    public String getEstatisticas(String nivel, String codigoComponente) throws ConnectionException{
    	return getDados("/ensino-services/services/consulta/turmas/estatisticas",nivel + "/" + codigoComponente);
    }
    
    public  String getAvaliacaoInstitucionalDocente(Integer idUnidade, Integer ano, Integer periodo) throws ConnectionException,IdException{
    	if(idUnidade<0){
    		throw new IdException("ID menor que zero!"); 
    	}
    	return getDados("/ensino-services/services/consulta/avaliacaoInstitucional/docente",idUnidade + "/" + ano + "/" + periodo);
    }
    
    public  String getUnidadesAcademicas(String nome) throws ConnectionException{
    	return getDados("/unidades-services/services/consulta/unidade",nome);
    }
    
    public  String getAvaliacoesInstitucionaisDocentes(Integer codigoUnidade, Integer ano, Integer periodo) throws ConnectionException{
    	return getDados("/ensino-services/services/consulta/avaliacaoInstitucional/docente",codigoUnidade + "/" + ano + "/" + periodo);
    }
    
    private  String getDados(String urlIntermediaria, Integer complemento) throws ConnectionException{
    	return getDadosAux(urlIntermediaria,Integer.toString(complemento));
    }
    private  String getDados(String urlIntermediaria, Long complemento) throws ConnectionException{
    	return getDadosAux(urlIntermediaria,Long.toString(complemento));
    }
    
    private  String getDados(String urlIntermediaria, String complemento) throws ConnectionException{
    	return getDadosAux(urlIntermediaria,complemento);
    }

    private String getDadosAux(String urlIntermediaria, String complemento) throws ConnectionException{
        String resultJson = "";
        try {
            OAuthClient client = new OAuthClient(new URLConnectionClient());

            OAuthClientRequest request =
                    OAuthClientRequest.tokenLocation(TOKEN_REQUEST_URL)
                            .setGrantType(GrantType.CLIENT_CREDENTIALS)
                            .setClientId(CLIENT_ID)
                            .setClientSecret(CLIENT_SECRET)
                            .buildQueryMessage();

            String token =
                    client.accessToken(request, OAuthJSONAccessTokenResponse.class)
                            .getAccessToken();

            HttpURLConnection resource_cxn =
                    (HttpURLConnection)(new URL(RESOURCE_URL_TPL + urlIntermediaria + "/" + complemento).openConnection());
            resource_cxn.addRequestProperty("Authorization", "Bearer " + token);

            InputStream resource = resource_cxn.getInputStream();
            
            BufferedReader r = new BufferedReader(new InputStreamReader(resource, "UTF-8"));
            String line = null;
            
            while ((line = r.readLine()) != null) {
            	resultJson += line;
            }System.out.println(RESOURCE_URL_TPL + urlIntermediaria + "/" + complemento);

        } catch (Exception exn) {
            System.out.println(exn.getMessage());
        	throw new ConnectionException();
        }

        return resultJson;
    }
}
