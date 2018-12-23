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
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_comment
comment|/**  * Base class for profile rules. Generally profiles are documents that define  * the PKI policy. In HDDS/Ozone world, we have chosen to make PKIs  * executable code. So if an end-user wants to use a custom profile or one of  * the existing profile like the list below, they are free to implement a  * custom profile.  *  *     PKIX - Internet PKI profile.  *     FPKI - (US) Federal PKI profile.  *     MISSI - US DoD profile.  *     ISO 15782 - Banking - Certificate Management Part 1: Public Key  *         Certificates.  *     TeleTrust/MailTrusT - German MailTrusT profile for TeleTrusT (it  *     really is  *         capitalised that way).  *     German SigG Profile - Profile to implement the German digital  *     signature law  *     ISIS Profile - Another German profile.  *     Australian Profile - Profile for the Australian PKAF  *     SS 61 43 31 Electronic ID Certificate - Swedish profile.  *     FINEID S3 - Finnish profile.  *     ANX Profile - Automotive Network Exchange profile.  *     Microsoft Profile - This isn't a real profile, but windows uses this.  */
end_comment

begin_interface
DECL|interface|PKIProfile
specifier|public
interface|interface
name|PKIProfile
block|{
comment|/**    * Returns the list of General Names  supported by this profile.    * @return - an Array of supported General Names by this certificate profile.    */
DECL|method|getGeneralNames ()
name|int
index|[]
name|getGeneralNames
parameter_list|()
function_decl|;
comment|/**    * Checks if a given General Name is permitted in this profile.    * @param generalName - General name.    * @return true if it is allowed, false otherwise.    */
DECL|method|isSupportedGeneralName (int generalName)
name|boolean
name|isSupportedGeneralName
parameter_list|(
name|int
name|generalName
parameter_list|)
function_decl|;
comment|/**    * Allows the profile to dictate what value ranges are valid.    * @param type - Type of the General Name.    * @param value - Value of the General Name.    * @return - true if the value is permitted, false otherwise.    * @throws UnknownHostException - on Error in IP validation.    */
DECL|method|validateGeneralName (int type, String value)
name|boolean
name|validateGeneralName
parameter_list|(
name|int
name|type
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|UnknownHostException
function_decl|;
comment|/**    * Returns an array of Object identifiers for extensions supported by this    * profile.    * @return an Array of ASN1ObjectIdentifier for the supported extensions.    */
DECL|method|getSupportedExtensions ()
name|ASN1ObjectIdentifier
index|[]
name|getSupportedExtensions
parameter_list|()
function_decl|;
comment|/**    * Checks if the this extension is permitted in this profile.    * @param extension - Extension to check for.    * @return - true if this extension is supported, false otherwise.    */
DECL|method|isSupportedExtension (Extension extension)
name|boolean
name|isSupportedExtension
parameter_list|(
name|Extension
name|extension
parameter_list|)
function_decl|;
comment|/**    * Checks if the extension has the value which this profile approves.    * @param extension - Extension to validate.    * @return - True if the extension is acceptable, false otherwise.    */
DECL|method|validateExtension (Extension extension)
name|boolean
name|validateExtension
parameter_list|(
name|Extension
name|extension
parameter_list|)
function_decl|;
comment|/**    * Validate the Extended Key Usage.    * @param id - KeyPurpose ID    * @return true, if this is a supported Purpose, false otherwise.    */
DECL|method|validateExtendedKeyUsage (KeyPurposeId id)
name|boolean
name|validateExtendedKeyUsage
parameter_list|(
name|KeyPurposeId
name|id
parameter_list|)
function_decl|;
comment|/**    * Returns the permitted Key usage mask while using this profile.    * @return KeyUsage    */
DECL|method|getKeyUsage ()
name|KeyUsage
name|getKeyUsage
parameter_list|()
function_decl|;
comment|/**    * Gets the supported list of RDNs supported by this profile.    * @return Array of RDNs.    */
DECL|method|getRDNs ()
name|RDN
index|[]
name|getRDNs
parameter_list|()
function_decl|;
comment|/**    * Returns true if this Relative Distinguished Name component is allowed in    * this profile.    * @param distinguishedName - RDN to check.    * @return boolean, True if this RDN is allowed, false otherwise.    */
DECL|method|isValidRDN (RDN distinguishedName)
name|boolean
name|isValidRDN
parameter_list|(
name|RDN
name|distinguishedName
parameter_list|)
function_decl|;
comment|/**    * Allows the profile to control the value set of the RDN. Profile can    * reject a RDN name if needed.    * @param name - RDN.    * @return true if the name is acceptable to this profile, false otherwise.    */
DECL|method|validateRDN (RDN name)
name|boolean
name|validateRDN
parameter_list|(
name|RDN
name|name
parameter_list|)
function_decl|;
comment|/**    * True if the profile we are checking is for issuing a CA certificate.    * @return  True, if the profile used is for CA, false otherwise.    */
DECL|method|isCA ()
name|boolean
name|isCA
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

