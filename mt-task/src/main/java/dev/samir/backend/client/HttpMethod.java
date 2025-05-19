package dev.samir.backend.client;

/**
 * https://tools.ietf.org/html/rfc7231#section-4.3 (Previous RFC: https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html)
 * <p>
 * The methods description is from PREVIOUS RFC with different words explaining the same concept from CURRENT RFC. 
 * 
 * @author Scheide, Samir
 */
public enum HttpMethod {
  
  /**
   * <p>
   * The <b>GET</b> method means retrieve whatever information (in the form of an entity) is identified by the Request-URI. 
   * If the Request-URI refers to a data-producing process, it is the produced data which shall be returned as the entity in the response and not the source text of the process, 
   * unless that text happens to be the output of the process.
   * </p>
   * <p>
   * The semantics of the <b>GET</b> method change to a "<i>conditional <b>GET</b></i>" if the request message includes an <b>IF-MODIFIED-SINCE</b>, <b>IF-UNMODIFIED-SINCE</b>, <b>IF-MATCH</b>, <b>IF-NONE-MATCH</b>, or <b>IF-RANGE</b> header field. 
   * A conditional <b>GET</b> method requests that the entity be transferred only under the circumstances described by the conditional header field(s). 
   * The conditional <b>GET</b> method is intended to reduce unnecessary network usage by allowing cached entities to be refreshed without requiring multiple requests or transferring data already held by the client.
   * <p>
   * The semantics of the <b>GET</b> method change to a "<i>partial <b>GET</b></i>" if the request message includes a <b>RANGE</b> header field. 
   * A partial <b>GET</b> requests that only part of the entity be transferred.
   * The partial <b>GET</b> method is intended to reduce unnecessary network usage by allowing partially-retrieved entities to be completed without transferring data already held by the client.    
   */
  GET; 
    
}