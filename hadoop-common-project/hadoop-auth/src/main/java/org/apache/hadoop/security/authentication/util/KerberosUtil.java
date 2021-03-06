begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authentication
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|PlatformName
operator|.
name|IBM_JAVA
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|IllegalCharsetNameException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerby
operator|.
name|kerberos
operator|.
name|kerb
operator|.
name|keytab
operator|.
name|Keytab
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerby
operator|.
name|kerberos
operator|.
name|kerb
operator|.
name|type
operator|.
name|base
operator|.
name|PrincipalName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|GSSException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|Oid
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|kerberos
operator|.
name|KerberosPrincipal
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|kerberos
operator|.
name|KerberosTicket
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|kerberos
operator|.
name|KeyTab
import|;
end_import

begin_class
DECL|class|KerberosUtil
specifier|public
class|class
name|KerberosUtil
block|{
comment|/* Return the Kerberos login module name */
DECL|method|getKrb5LoginModuleName ()
specifier|public
specifier|static
name|String
name|getKrb5LoginModuleName
parameter_list|()
block|{
return|return
operator|(
name|IBM_JAVA
operator|)
condition|?
literal|"com.ibm.security.auth.module.Krb5LoginModule"
else|:
literal|"com.sun.security.auth.module.Krb5LoginModule"
return|;
block|}
DECL|field|GSS_SPNEGO_MECH_OID
specifier|public
specifier|static
specifier|final
name|Oid
name|GSS_SPNEGO_MECH_OID
init|=
name|getNumericOidInstance
argument_list|(
literal|"1.3.6.1.5.5.2"
argument_list|)
decl_stmt|;
DECL|field|GSS_KRB5_MECH_OID
specifier|public
specifier|static
specifier|final
name|Oid
name|GSS_KRB5_MECH_OID
init|=
name|getNumericOidInstance
argument_list|(
literal|"1.2.840.113554.1.2.2"
argument_list|)
decl_stmt|;
DECL|field|NT_GSS_KRB5_PRINCIPAL_OID
specifier|public
specifier|static
specifier|final
name|Oid
name|NT_GSS_KRB5_PRINCIPAL_OID
init|=
name|getNumericOidInstance
argument_list|(
literal|"1.2.840.113554.1.2.2.1"
argument_list|)
decl_stmt|;
comment|// numeric oids will never generate a GSSException for a malformed oid.
comment|// use to initialize statics.
DECL|method|getNumericOidInstance (String oidName)
specifier|private
specifier|static
name|Oid
name|getNumericOidInstance
parameter_list|(
name|String
name|oidName
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|Oid
argument_list|(
name|oidName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|GSSException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|getOidInstance (String oidName)
specifier|public
specifier|static
name|Oid
name|getOidInstance
parameter_list|(
name|String
name|oidName
parameter_list|)
throws|throws
name|ClassNotFoundException
throws|,
name|GSSException
throws|,
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|oidClass
decl_stmt|;
if|if
condition|(
name|IBM_JAVA
condition|)
block|{
if|if
condition|(
literal|"NT_GSS_KRB5_PRINCIPAL"
operator|.
name|equals
argument_list|(
name|oidName
argument_list|)
condition|)
block|{
comment|// IBM JDK GSSUtil class does not have field for krb5 principal oid
return|return
operator|new
name|Oid
argument_list|(
literal|"1.2.840.113554.1.2.2.1"
argument_list|)
return|;
block|}
name|oidClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.ibm.security.jgss.GSSUtil"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|oidClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.security.jgss.GSSUtil"
argument_list|)
expr_stmt|;
block|}
name|Field
name|oidField
init|=
name|oidClass
operator|.
name|getDeclaredField
argument_list|(
name|oidName
argument_list|)
decl_stmt|;
return|return
operator|(
name|Oid
operator|)
name|oidField
operator|.
name|get
argument_list|(
name|oidClass
argument_list|)
return|;
block|}
comment|/**    * Return the default realm for this JVM.    *    * @return The default realm    * @throws IllegalArgumentException If the default realm does not exist.    * @throws ClassNotFoundException Not thrown. Exists for compatibility.    * @throws NoSuchMethodException Not thrown. Exists for compatibility.    * @throws IllegalAccessException Not thrown. Exists for compatibility.    * @throws InvocationTargetException Not thrown. Exists for compatibility.    */
DECL|method|getDefaultRealm ()
specifier|public
specifier|static
name|String
name|getDefaultRealm
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|NoSuchMethodException
throws|,
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|InvocationTargetException
block|{
comment|// Any name is okay.
return|return
operator|new
name|KerberosPrincipal
argument_list|(
literal|"tmp"
argument_list|,
literal|1
argument_list|)
operator|.
name|getRealm
argument_list|()
return|;
block|}
comment|/**    * Return the default realm for this JVM.    * If the default realm does not exist, this method returns null.    *    * @return The default realm    */
DECL|method|getDefaultRealmProtected ()
specifier|public
specifier|static
name|String
name|getDefaultRealmProtected
parameter_list|()
block|{
try|try
block|{
return|return
name|getDefaultRealm
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//silently catch everything
return|return
literal|null
return|;
block|}
block|}
comment|/*    * For a Service Host Principal specification, map the host's domain    * to kerberos realm, as specified by krb5.conf [domain_realm] mappings.    * Unfortunately the mapping routines are private to the security.krb5    * package, so have to construct a PrincipalName instance to derive the realm.    *    * Many things can go wrong with Kerberos configuration, and this is not    * the place to be throwing exceptions to help debug them.  Nor do we choose    * to make potentially voluminous logs on every call to a communications API.    * So we simply swallow all exceptions from the underlying libraries and    * return null if we can't get a good value for the realmString.    *    * @param shortprinc A service principal name with host fqdn as instance, e.g.    *     "HTTP/myhost.mydomain"    * @return String value of Kerberos realm, mapped from host fqdn    *     May be default realm, or may be null.    */
DECL|method|getDomainRealm (String shortprinc)
specifier|public
specifier|static
name|String
name|getDomainRealm
parameter_list|(
name|String
name|shortprinc
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|classRef
decl_stmt|;
name|Object
name|principalName
decl_stmt|;
comment|//of type sun.security.krb5.PrincipalName or IBM equiv
name|String
name|realmString
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|IBM_JAVA
condition|)
block|{
name|classRef
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.ibm.security.krb5.PrincipalName"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|classRef
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.security.krb5.PrincipalName"
argument_list|)
expr_stmt|;
block|}
name|int
name|tKrbNtSrvHst
init|=
name|classRef
operator|.
name|getField
argument_list|(
literal|"KRB_NT_SRV_HST"
argument_list|)
operator|.
name|getInt
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|principalName
operator|=
name|classRef
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|shortprinc
argument_list|,
name|tKrbNtSrvHst
argument_list|)
expr_stmt|;
name|realmString
operator|=
operator|(
name|String
operator|)
name|classRef
operator|.
name|getMethod
argument_list|(
literal|"getRealmString"
argument_list|,
operator|new
name|Class
index|[
literal|0
index|]
argument_list|)
operator|.
name|invoke
argument_list|(
name|principalName
argument_list|,
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|rte
parameter_list|)
block|{
comment|//silently catch everything
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//silently return default realm (which may itself be null)
block|}
if|if
condition|(
literal|null
operator|==
name|realmString
operator|||
name|realmString
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
return|return
name|getDefaultRealmProtected
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|realmString
return|;
block|}
block|}
comment|/* Return fqdn of the current host */
DECL|method|getLocalHostName ()
specifier|public
specifier|static
name|String
name|getLocalHostName
parameter_list|()
throws|throws
name|UnknownHostException
block|{
return|return
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
return|;
block|}
comment|/**    * Create Kerberos principal for a given service and hostname,    * inferring realm from the fqdn of the hostname. It converts    * hostname to lower case. If hostname is null or "0.0.0.0", it uses    * dynamically looked-up fqdn of the current host instead.    * If domain_realm mappings are inadequately specified, it will    * use default_realm, per usual Kerberos behavior.    * If default_realm also gives a null value, then a principal    * without realm will be returned, which by Kerberos definitions is    * just another way to specify default realm.    *    * @param service    *          Service for which you want to generate the principal.    * @param hostname    *          Fully-qualified domain name.    * @return Converted Kerberos principal name.    * @throws UnknownHostException    *           If no IP address for the local host could be found.    */
DECL|method|getServicePrincipal (String service, String hostname)
specifier|public
specifier|static
specifier|final
name|String
name|getServicePrincipal
parameter_list|(
name|String
name|service
parameter_list|,
name|String
name|hostname
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|String
name|fqdn
init|=
name|hostname
decl_stmt|;
name|String
name|shortprinc
init|=
literal|null
decl_stmt|;
name|String
name|realmString
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fqdn
operator|||
name|fqdn
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|fqdn
operator|.
name|equals
argument_list|(
literal|"0.0.0.0"
argument_list|)
condition|)
block|{
name|fqdn
operator|=
name|getLocalHostName
argument_list|()
expr_stmt|;
block|}
comment|// convert hostname to lowercase as kerberos does not work with hostnames
comment|// with uppercase characters.
name|fqdn
operator|=
name|fqdn
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|shortprinc
operator|=
name|service
operator|+
literal|"/"
operator|+
name|fqdn
expr_stmt|;
comment|// Obtain the realm name inferred from the domain of the host
name|realmString
operator|=
name|getDomainRealm
argument_list|(
name|shortprinc
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|realmString
operator|||
name|realmString
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
return|return
name|shortprinc
return|;
block|}
else|else
block|{
return|return
name|shortprinc
operator|+
literal|"@"
operator|+
name|realmString
return|;
block|}
block|}
comment|/**    * Get all the unique principals present in the keytabfile.    *     * @param keytabFileName     *          Name of the keytab file to be read.    * @return list of unique principals in the keytab.    * @throws IOException     *          If keytab entries cannot be read from the file.    */
DECL|method|getPrincipalNames (String keytabFileName)
specifier|static
specifier|final
name|String
index|[]
name|getPrincipalNames
parameter_list|(
name|String
name|keytabFileName
parameter_list|)
throws|throws
name|IOException
block|{
name|Keytab
name|keytab
init|=
name|Keytab
operator|.
name|loadKeytab
argument_list|(
operator|new
name|File
argument_list|(
name|keytabFileName
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PrincipalName
argument_list|>
name|entries
init|=
name|keytab
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
for|for
control|(
name|PrincipalName
name|entry
range|:
name|entries
control|)
block|{
name|principals
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|principals
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**    * Get all the unique principals from keytabfile which matches a pattern.    *     * @param keytab Name of the keytab file to be read.    * @param pattern pattern to be matched.    * @return list of unique principals which matches the pattern.    * @throws IOException if cannot get the principal name    */
DECL|method|getPrincipalNames (String keytab, Pattern pattern)
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|getPrincipalNames
parameter_list|(
name|String
name|keytab
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|principals
init|=
name|getPrincipalNames
argument_list|(
name|keytab
argument_list|)
decl_stmt|;
if|if
condition|(
name|principals
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|matchingPrincipals
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|principal
range|:
name|principals
control|)
block|{
if|if
condition|(
name|pattern
operator|.
name|matcher
argument_list|(
name|principal
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|matchingPrincipals
operator|.
name|add
argument_list|(
name|principal
argument_list|)
expr_stmt|;
block|}
block|}
name|principals
operator|=
name|matchingPrincipals
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|principals
return|;
block|}
comment|/**    * Check if the subject contains Kerberos keytab related objects.    * The Kerberos keytab object attached in subject has been changed    * from KerberosKey (JDK 7) to KeyTab (JDK 8)    *    *    * @param subject subject to be checked    * @return true if the subject contains Kerberos keytab    */
DECL|method|hasKerberosKeyTab (Subject subject)
specifier|public
specifier|static
name|boolean
name|hasKerberosKeyTab
parameter_list|(
name|Subject
name|subject
parameter_list|)
block|{
return|return
operator|!
name|subject
operator|.
name|getPrivateCredentials
argument_list|(
name|KeyTab
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * Check if the subject contains Kerberos ticket.    *    *    * @param subject subject to be checked    * @return true if the subject contains Kerberos ticket    */
DECL|method|hasKerberosTicket (Subject subject)
specifier|public
specifier|static
name|boolean
name|hasKerberosTicket
parameter_list|(
name|Subject
name|subject
parameter_list|)
block|{
return|return
operator|!
name|subject
operator|.
name|getPrivateCredentials
argument_list|(
name|KerberosTicket
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * Extract the TGS server principal from the given gssapi kerberos or spnego    * wrapped token.    * @param rawToken bytes of the gss token    * @return String of server principal    * @throws IllegalArgumentException if token is undecodable    */
DECL|method|getTokenServerName (byte[] rawToken)
specifier|public
specifier|static
name|String
name|getTokenServerName
parameter_list|(
name|byte
index|[]
name|rawToken
parameter_list|)
block|{
comment|// subsequent comments include only relevant portions of the kerberos
comment|// DER encoding that will be extracted.
name|DER
name|token
init|=
operator|new
name|DER
argument_list|(
name|rawToken
argument_list|)
decl_stmt|;
comment|// InitialContextToken ::= [APPLICATION 0] IMPLICIT SEQUENCE {
comment|//     mech   OID
comment|//     mech-token  (NegotiationToken or InnerContextToken)
comment|// }
name|DER
name|oid
init|=
name|token
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|oid
operator|.
name|equals
argument_list|(
name|DER
operator|.
name|SPNEGO_MECH_OID
argument_list|)
condition|)
block|{
comment|// NegotiationToken ::= CHOICE {
comment|//     neg-token-init[0] NegTokenInit
comment|// }
comment|// NegTokenInit ::= SEQUENCE {
comment|//     mech-token[2]     InitialContextToken
comment|// }
name|token
operator|=
name|token
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|(
literal|0xa0
argument_list|,
literal|0x30
argument_list|,
literal|0xa2
argument_list|,
literal|0x04
argument_list|)
operator|.
name|next
argument_list|()
expr_stmt|;
name|oid
operator|=
name|token
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|oid
operator|.
name|equals
argument_list|(
name|DER
operator|.
name|KRB5_MECH_OID
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Malformed gss token"
argument_list|)
throw|;
block|}
comment|// InnerContextToken ::= {
comment|//     token-id[1]
comment|//     AP-REQ
comment|// }
if|if
condition|(
name|token
operator|.
name|next
argument_list|()
operator|.
name|getTag
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not an AP-REQ token"
argument_list|)
throw|;
block|}
comment|// AP-REQ ::= [APPLICATION 14] SEQUENCE {
comment|//     ticket[3]      Ticket
comment|// }
name|DER
name|ticket
init|=
name|token
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|(
literal|0x6e
argument_list|,
literal|0x30
argument_list|,
literal|0xa3
argument_list|,
literal|0x61
argument_list|,
literal|0x30
argument_list|)
decl_stmt|;
comment|// Ticket ::= [APPLICATION 1] SEQUENCE {
comment|//     realm[1]       String
comment|//     sname[2]       PrincipalName
comment|// }
comment|// PrincipalName ::= SEQUENCE {
comment|//     name-string[1] SEQUENCE OF String
comment|// }
name|String
name|realm
init|=
name|ticket
operator|.
name|get
argument_list|(
literal|0xa1
argument_list|,
literal|0x1b
argument_list|)
operator|.
name|getAsString
argument_list|()
decl_stmt|;
name|DER
name|names
init|=
name|ticket
operator|.
name|get
argument_list|(
literal|0xa2
argument_list|,
literal|0x30
argument_list|,
literal|0xa1
argument_list|,
literal|0x30
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|names
operator|.
name|next
argument_list|()
operator|.
name|getAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|'@'
argument_list|)
operator|.
name|append
argument_list|(
name|realm
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// basic ASN.1 DER decoder to traverse encoded byte arrays.
DECL|class|DER
specifier|private
specifier|static
class|class
name|DER
implements|implements
name|Iterator
argument_list|<
name|DER
argument_list|>
block|{
DECL|field|SPNEGO_MECH_OID
specifier|static
specifier|final
name|DER
name|SPNEGO_MECH_OID
init|=
name|getDER
argument_list|(
name|GSS_SPNEGO_MECH_OID
argument_list|)
decl_stmt|;
DECL|field|KRB5_MECH_OID
specifier|static
specifier|final
name|DER
name|KRB5_MECH_OID
init|=
name|getDER
argument_list|(
name|GSS_KRB5_MECH_OID
argument_list|)
decl_stmt|;
DECL|method|getDER (Oid oid)
specifier|private
specifier|static
name|DER
name|getDER
parameter_list|(
name|Oid
name|oid
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|DER
argument_list|(
name|oid
operator|.
name|getDER
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|GSSException
name|ex
parameter_list|)
block|{
comment|// won't happen.  a proper OID is encodable.
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|field|tag
specifier|private
specifier|final
name|int
name|tag
decl_stmt|;
DECL|field|bb
specifier|private
specifier|final
name|ByteBuffer
name|bb
decl_stmt|;
DECL|method|DER (byte[] buf)
name|DER
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|)
block|{
name|this
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buf
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|DER (ByteBuffer srcbb)
name|DER
parameter_list|(
name|ByteBuffer
name|srcbb
parameter_list|)
block|{
name|tag
operator|=
name|srcbb
operator|.
name|get
argument_list|()
operator|&
literal|0xff
expr_stmt|;
name|int
name|length
init|=
name|readLength
argument_list|(
name|srcbb
argument_list|)
decl_stmt|;
name|bb
operator|=
name|srcbb
operator|.
name|slice
argument_list|()
expr_stmt|;
name|bb
operator|.
name|limit
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|srcbb
operator|.
name|position
argument_list|(
name|srcbb
operator|.
name|position
argument_list|()
operator|+
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|getTag ()
name|int
name|getTag
parameter_list|()
block|{
return|return
name|tag
return|;
block|}
comment|// standard ASN.1 encoding.
DECL|method|readLength (ByteBuffer bb)
specifier|private
specifier|static
name|int
name|readLength
parameter_list|(
name|ByteBuffer
name|bb
parameter_list|)
block|{
name|int
name|length
init|=
name|bb
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|length
operator|&
operator|(
name|byte
operator|)
literal|0x80
operator|)
operator|!=
literal|0
condition|)
block|{
name|int
name|varlength
init|=
name|length
operator|&
literal|0x7f
decl_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|varlength
condition|;
name|i
operator|++
control|)
block|{
name|length
operator|=
operator|(
name|length
operator|<<
literal|8
operator|)
operator||
operator|(
name|bb
operator|.
name|get
argument_list|()
operator|&
literal|0xff
operator|)
expr_stmt|;
block|}
block|}
return|return
name|length
return|;
block|}
DECL|method|choose (int subtag)
name|DER
name|choose
parameter_list|(
name|int
name|subtag
parameter_list|)
block|{
while|while
condition|(
name|hasNext
argument_list|()
condition|)
block|{
name|DER
name|der
init|=
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|der
operator|.
name|getTag
argument_list|()
operator|==
name|subtag
condition|)
block|{
return|return
name|der
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|get (int... tags)
name|DER
name|get
parameter_list|(
name|int
modifier|...
name|tags
parameter_list|)
block|{
name|DER
name|der
init|=
name|this
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tags
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|expectedTag
init|=
name|tags
index|[
name|i
index|]
decl_stmt|;
comment|// lookup for exact match, else scan if it's sequenced.
if|if
condition|(
name|der
operator|.
name|getTag
argument_list|()
operator|!=
name|expectedTag
condition|)
block|{
name|der
operator|=
name|der
operator|.
name|hasNext
argument_list|()
condition|?
name|der
operator|.
name|choose
argument_list|(
name|expectedTag
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|der
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Tag not found:"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<=
name|i
condition|;
name|ii
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" 0x"
argument_list|)
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|tags
index|[
name|ii
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|der
return|;
block|}
DECL|method|getAsString ()
name|String
name|getAsString
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|String
argument_list|(
name|bb
operator|.
name|array
argument_list|()
argument_list|,
name|bb
operator|.
name|arrayOffset
argument_list|()
operator|+
name|bb
operator|.
name|position
argument_list|()
argument_list|,
name|bb
operator|.
name|remaining
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalCharsetNameException
argument_list|(
literal|"UTF-8"
argument_list|)
throw|;
comment|// won't happen.
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|tag
operator|+
name|bb
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|(
name|o
operator|instanceof
name|DER
operator|)
operator|&&
name|tag
operator|==
operator|(
operator|(
name|DER
operator|)
name|o
operator|)
operator|.
name|tag
operator|&&
name|bb
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|DER
operator|)
name|o
operator|)
operator|.
name|bb
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
comment|// it's a sequence or an embedded octet.
return|return
operator|(
operator|(
name|tag
operator|&
literal|0x30
operator|)
operator|!=
literal|0
operator|||
name|tag
operator|==
literal|0x04
operator|)
operator|&&
name|bb
operator|.
name|hasRemaining
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|DER
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
operator|new
name|DER
argument_list|(
name|bb
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"[tag=0x"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|tag
argument_list|)
operator|+
literal|" bb="
operator|+
name|bb
operator|+
literal|"]"
return|;
block|}
block|}
block|}
end_class

end_unit

