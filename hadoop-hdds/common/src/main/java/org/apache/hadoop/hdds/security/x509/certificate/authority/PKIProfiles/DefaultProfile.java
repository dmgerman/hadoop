begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.certificate.authority.PKIProfiles
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|certificate
operator|.
name|authority
operator|.
name|PKIProfiles
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|validator
operator|.
name|routines
operator|.
name|DomainValidator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|ASN1ObjectIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x500
operator|.
name|RDN
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|ExtendedKeyUsage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|Extension
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|GeneralName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|GeneralNames
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|KeyPurposeId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|KeyUsage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|DatatypeConverter
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
name|util
operator|.
name|AbstractMap
operator|.
name|SimpleEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|Map
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
name|function
operator|.
name|BiFunction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Boolean
operator|.
name|TRUE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|KeyPurposeId
operator|.
name|id_kp_clientAuth
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|KeyPurposeId
operator|.
name|id_kp_serverAuth
import|;
end_import

begin_comment
comment|/**  * Ozone PKI profile.  *<p>  * This PKI profile is invoked by SCM CA to make sure that certificates issued  * by SCM CA are constrained  */
end_comment

begin_class
DECL|class|DefaultProfile
specifier|public
class|class
name|DefaultProfile
implements|implements
name|PKIProfile
block|{
specifier|static
specifier|final
name|BiFunction
argument_list|<
name|Extension
argument_list|,
name|PKIProfile
argument_list|,
name|Boolean
argument_list|>
DECL|field|VALIDATE_KEY_USAGE
name|VALIDATE_KEY_USAGE
init|=
name|DefaultProfile
operator|::
name|validateKeyUsage
decl_stmt|;
specifier|static
specifier|final
name|BiFunction
argument_list|<
name|Extension
argument_list|,
name|PKIProfile
argument_list|,
name|Boolean
argument_list|>
DECL|field|VALIDATE_AUTHORITY_KEY_IDENTIFIER
name|VALIDATE_AUTHORITY_KEY_IDENTIFIER
init|=
parameter_list|(
name|e
parameter_list|,
name|b
parameter_list|)
lambda|->
name|TRUE
decl_stmt|;
specifier|static
specifier|final
name|BiFunction
argument_list|<
name|Extension
argument_list|,
name|PKIProfile
argument_list|,
name|Boolean
argument_list|>
DECL|field|VALIDATE_LOGO_TYPE
name|VALIDATE_LOGO_TYPE
init|=
parameter_list|(
name|e
parameter_list|,
name|b
parameter_list|)
lambda|->
name|TRUE
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DefaultProfile
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|BiFunction
argument_list|<
name|Extension
argument_list|,
name|PKIProfile
argument_list|,
name|Boolean
argument_list|>
DECL|field|VALIDATE_SAN
name|VALIDATE_SAN
init|=
name|DefaultProfile
operator|::
name|validateSubjectAlternativeName
decl_stmt|;
specifier|static
specifier|final
name|BiFunction
argument_list|<
name|Extension
argument_list|,
name|PKIProfile
argument_list|,
name|Boolean
argument_list|>
DECL|field|VALIDATE_EXTENDED_KEY_USAGE
name|VALIDATE_EXTENDED_KEY_USAGE
init|=
name|DefaultProfile
operator|::
name|validateExtendedKeyUsage
decl_stmt|;
comment|// If we decide to add more General Names, we should add those here and
comment|// also update the logic in validateGeneralName function.
DECL|field|GENERAL_NAMES
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|GENERAL_NAMES
init|=
block|{
name|GeneralName
operator|.
name|dNSName
block|,
name|GeneralName
operator|.
name|iPAddress
block|,   }
decl_stmt|;
comment|// Map that handles all the Extensions lookup and validations.
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|ASN1ObjectIdentifier
argument_list|,
name|BiFunction
argument_list|<
name|Extension
argument_list|,
DECL|field|EXTENSIONS_MAP
name|PKIProfile
argument_list|,
name|Boolean
argument_list|>
argument_list|>
name|EXTENSIONS_MAP
init|=
name|Stream
operator|.
name|of
argument_list|(
operator|new
name|SimpleEntry
argument_list|<>
argument_list|(
name|Extension
operator|.
name|keyUsage
argument_list|,
name|VALIDATE_KEY_USAGE
argument_list|)
argument_list|,
operator|new
name|SimpleEntry
argument_list|<>
argument_list|(
name|Extension
operator|.
name|subjectAlternativeName
argument_list|,
name|VALIDATE_SAN
argument_list|)
argument_list|,
operator|new
name|SimpleEntry
argument_list|<>
argument_list|(
name|Extension
operator|.
name|authorityKeyIdentifier
argument_list|,
name|VALIDATE_AUTHORITY_KEY_IDENTIFIER
argument_list|)
argument_list|,
operator|new
name|SimpleEntry
argument_list|<>
argument_list|(
name|Extension
operator|.
name|extendedKeyUsage
argument_list|,
name|VALIDATE_EXTENDED_KEY_USAGE
argument_list|)
argument_list|,
comment|// Ozone certs are issued only for the use of Ozone.
comment|// However, some users will discover that this is a full scale CA
comment|// and decide to mis-use these certs for other purposes.
comment|// To discourage usage of these certs for other purposes, we can leave
comment|// the Ozone Logo inside these certs. So if a browser is used to
comment|// connect these logos will show up.
comment|// https://www.ietf.org/rfc/rfc3709.txt
operator|new
name|SimpleEntry
argument_list|<>
argument_list|(
name|Extension
operator|.
name|logoType
argument_list|,
name|VALIDATE_LOGO_TYPE
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|SimpleEntry
operator|::
name|getKey
argument_list|,
name|SimpleEntry
operator|::
name|getValue
argument_list|)
argument_list|)
decl_stmt|;
comment|// If we decide to add more General Names, we should add those here and
comment|// also update the logic in validateGeneralName function.
DECL|field|EXTENDED_KEY_USAGE
specifier|private
specifier|static
specifier|final
name|KeyPurposeId
index|[]
name|EXTENDED_KEY_USAGE
init|=
block|{
name|id_kp_serverAuth
block|,
comment|// TLS Web server authentication
name|id_kp_clientAuth
block|,
comment|// TLS Web client authentication
block|}
decl_stmt|;
DECL|field|extendKeyPurposeSet
specifier|private
specifier|final
name|Set
argument_list|<
name|KeyPurposeId
argument_list|>
name|extendKeyPurposeSet
decl_stmt|;
DECL|field|generalNameSet
specifier|private
name|Set
argument_list|<
name|Integer
argument_list|>
name|generalNameSet
decl_stmt|;
comment|/**    * Construct DefaultProfile.    */
DECL|method|DefaultProfile ()
specifier|public
name|DefaultProfile
parameter_list|()
block|{
name|generalNameSet
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|val
range|:
name|GENERAL_NAMES
control|)
block|{
name|generalNameSet
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|extendKeyPurposeSet
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|EXTENDED_KEY_USAGE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * This function validates that the KeyUsage Bits are subset of the Bits    * permitted by the ozone profile.    *    * @param ext - KeyUsage Extension.    * @param profile - PKI Profile - In this case this profile.    * @return True, if the request key usage is a subset, false otherwise.    */
DECL|method|validateKeyUsage (Extension ext, PKIProfile profile)
specifier|private
specifier|static
name|Boolean
name|validateKeyUsage
parameter_list|(
name|Extension
name|ext
parameter_list|,
name|PKIProfile
name|profile
parameter_list|)
block|{
name|KeyUsage
name|keyUsage
init|=
name|profile
operator|.
name|getKeyUsage
argument_list|()
decl_stmt|;
name|KeyUsage
name|requestedUsage
init|=
name|KeyUsage
operator|.
name|getInstance
argument_list|(
name|ext
operator|.
name|getParsedValue
argument_list|()
argument_list|)
decl_stmt|;
name|BitSet
name|profileBitSet
init|=
name|BitSet
operator|.
name|valueOf
argument_list|(
name|keyUsage
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|BitSet
name|requestBitSet
init|=
name|BitSet
operator|.
name|valueOf
argument_list|(
name|requestedUsage
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
comment|// Check if the requestBitSet is a subset of profileBitSet
comment|//  p& r == r should be equal if it is a subset.
name|profileBitSet
operator|.
name|and
argument_list|(
name|requestBitSet
argument_list|)
expr_stmt|;
return|return
name|profileBitSet
operator|.
name|equals
argument_list|(
name|requestBitSet
argument_list|)
return|;
block|}
comment|/**    * Validates the SubjectAlternative names in the Certificate.    *    * @param ext - Extension - SAN, which allows us to get the SAN names.    * @param profile - This profile.    * @return - True if the request contains only SANs, General names that we    * support. False otherwise.    */
DECL|method|validateSubjectAlternativeName (Extension ext, PKIProfile profile)
specifier|private
specifier|static
name|Boolean
name|validateSubjectAlternativeName
parameter_list|(
name|Extension
name|ext
parameter_list|,
name|PKIProfile
name|profile
parameter_list|)
block|{
if|if
condition|(
name|ext
operator|.
name|isCritical
argument_list|()
condition|)
block|{
comment|// SAN extensions should not be marked as critical under ozone profile.
name|LOG
operator|.
name|error
argument_list|(
literal|"SAN extension marked as critical in the Extension. {}"
argument_list|,
name|GeneralNames
operator|.
name|getInstance
argument_list|(
name|ext
operator|.
name|getParsedValue
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|GeneralNames
name|generalNames
init|=
name|GeneralNames
operator|.
name|getInstance
argument_list|(
name|ext
operator|.
name|getParsedValue
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|GeneralName
name|name
range|:
name|generalNames
operator|.
name|getNames
argument_list|()
control|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|profile
operator|.
name|validateGeneralName
argument_list|(
name|name
operator|.
name|getTagNo
argument_list|()
argument_list|,
name|name
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"IP address validation failed."
operator|+
name|name
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * This function validates that the KeyUsage Bits are subset of the Bits    * permitted by the ozone profile.    *    * @param ext - KeyUsage Extension.    * @param profile - PKI Profile - In this case this profile.    * @return True, if the request key usage is a subset, false otherwise.    */
DECL|method|validateExtendedKeyUsage (Extension ext, PKIProfile profile)
specifier|private
specifier|static
name|Boolean
name|validateExtendedKeyUsage
parameter_list|(
name|Extension
name|ext
parameter_list|,
name|PKIProfile
name|profile
parameter_list|)
block|{
if|if
condition|(
name|ext
operator|.
name|isCritical
argument_list|()
condition|)
block|{
comment|// https://tools.ietf.org/html/rfc5280#section-4.2.1.12
comment|// Ozone profile opts to mark this extension as non-critical.
name|LOG
operator|.
name|error
argument_list|(
literal|"Extended Key usage marked as critical."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|ExtendedKeyUsage
name|extendedKeyUsage
init|=
name|ExtendedKeyUsage
operator|.
name|getInstance
argument_list|(
name|ext
operator|.
name|getParsedValue
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|KeyPurposeId
name|id
range|:
name|extendedKeyUsage
operator|.
name|getUsages
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|profile
operator|.
name|validateExtendedKeyUsage
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getGeneralNames ()
specifier|public
name|int
index|[]
name|getGeneralNames
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|GENERAL_NAMES
argument_list|,
literal|0
argument_list|,
name|GENERAL_NAMES
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|isSupportedGeneralName (int generalName)
specifier|public
name|boolean
name|isSupportedGeneralName
parameter_list|(
name|int
name|generalName
parameter_list|)
block|{
return|return
name|generalNameSet
operator|.
name|contains
argument_list|(
name|generalName
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|validateGeneralName (int type, String value)
specifier|public
name|boolean
name|validateGeneralName
parameter_list|(
name|int
name|type
parameter_list|,
name|String
name|value
parameter_list|)
block|{
comment|// TODO : We should add more validation for IP address, for example
comment|//  it matches the local network, and domain matches where the cluster
comment|//  exits.
if|if
condition|(
operator|!
name|isSupportedGeneralName
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|GeneralName
operator|.
name|iPAddress
case|:
comment|// We need DatatypeConverter conversion, since the original CSR encodes
comment|// an IP address int a Hex String, for example 8.8.8.8 is encoded as
comment|// #08080808. Value string is always preceded by "#", we will strip
comment|// that before passing it on.
comment|// getByAddress call converts the IP address to hostname/ipAddress format.
comment|// if the hostname cannot determined then it will be /ipAddress.
comment|// TODO: Fail? if we cannot resolve the Hostname?
try|try
block|{
specifier|final
name|InetAddress
name|byAddress
init|=
name|InetAddress
operator|.
name|getByAddress
argument_list|(
name|DatatypeConverter
operator|.
name|parseHexBinary
argument_list|(
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Host Name/IP Address : {}"
argument_list|,
name|byAddress
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
case|case
name|GeneralName
operator|.
name|dNSName
case|:
return|return
name|DomainValidator
operator|.
name|getInstance
argument_list|()
operator|.
name|isValid
argument_list|(
name|value
argument_list|)
return|;
default|default:
comment|// This should not happen, since it guarded via isSupportedGeneralName.
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected type in General Name (int value) : "
operator|+
name|type
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|validateExtendedKeyUsage (KeyPurposeId id)
specifier|public
name|boolean
name|validateExtendedKeyUsage
parameter_list|(
name|KeyPurposeId
name|id
parameter_list|)
block|{
return|return
name|extendKeyPurposeSet
operator|.
name|contains
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getSupportedExtensions ()
specifier|public
name|ASN1ObjectIdentifier
index|[]
name|getSupportedExtensions
parameter_list|()
block|{
return|return
name|EXTENSIONS_MAP
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|ASN1ObjectIdentifier
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|isSupportedExtension (Extension extension)
specifier|public
name|boolean
name|isSupportedExtension
parameter_list|(
name|Extension
name|extension
parameter_list|)
block|{
return|return
name|EXTENSIONS_MAP
operator|.
name|containsKey
argument_list|(
name|extension
operator|.
name|getExtnId
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|validateExtension (Extension extension)
specifier|public
name|boolean
name|validateExtension
parameter_list|(
name|Extension
name|extension
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|extension
argument_list|,
literal|"Extension cannot be null"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isSupportedExtension
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unsupported Extension found: {} "
argument_list|,
name|extension
operator|.
name|getExtnId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|BiFunction
argument_list|<
name|Extension
argument_list|,
name|PKIProfile
argument_list|,
name|Boolean
argument_list|>
name|func
init|=
name|EXTENSIONS_MAP
operator|.
name|get
argument_list|(
name|extension
operator|.
name|getExtnId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|func
operator|!=
literal|null
condition|)
block|{
return|return
name|func
operator|.
name|apply
argument_list|(
name|extension
argument_list|,
name|this
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getKeyUsage ()
specifier|public
name|KeyUsage
name|getKeyUsage
parameter_list|()
block|{
return|return
operator|new
name|KeyUsage
argument_list|(
name|KeyUsage
operator|.
name|digitalSignature
operator||
name|KeyUsage
operator|.
name|keyEncipherment
operator||
name|KeyUsage
operator|.
name|dataEncipherment
operator||
name|KeyUsage
operator|.
name|keyAgreement
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getRDNs ()
specifier|public
name|RDN
index|[]
name|getRDNs
parameter_list|()
block|{
return|return
operator|new
name|RDN
index|[
literal|0
index|]
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|isValidRDN (RDN distinguishedName)
specifier|public
name|boolean
name|isValidRDN
parameter_list|(
name|RDN
name|distinguishedName
parameter_list|)
block|{
comment|// TODO: Right now we just approve all strings.
return|return
literal|true
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|validateRDN (RDN name)
specifier|public
name|boolean
name|validateRDN
parameter_list|(
name|RDN
name|name
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|isCA ()
specifier|public
name|boolean
name|isCA
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

