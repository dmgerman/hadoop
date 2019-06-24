begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|Joiner
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|net
operator|.
name|InetSocketAddress
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
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPOutputStream
import|;
end_import

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
name|Strings
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
name|compress
operator|.
name|archivers
operator|.
name|tar
operator|.
name|TarArchiveEntry
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
name|compress
operator|.
name|archivers
operator|.
name|tar
operator|.
name|TarArchiveOutputStream
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
name|compress
operator|.
name|utils
operator|.
name|IOUtils
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
name|lang3
operator|.
name|RandomStringUtils
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
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|HddsServerUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
operator|.
name|ServerUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
operator|.
name|NetUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|HddsUtils
operator|.
name|getHostNameFromConfigKeys
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|HddsUtils
operator|.
name|getPortNumberFromConfigKeys
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_ADDRESS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_BIND_HOST_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTPS_ADDRESS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTPS_BIND_HOST_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTPS_BIND_PORT_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTP_ADDRESS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTP_BIND_HOST_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_HTTP_BIND_PORT_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_NODES_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_PORT_DEFAULT
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

begin_comment
comment|/**  * Stateless helper functions for the server and client side of OM  * communication.  */
end_comment

begin_class
DECL|class|OmUtils
specifier|public
specifier|final
class|class
name|OmUtils
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OmUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|OmUtils ()
specifier|private
name|OmUtils
parameter_list|()
block|{   }
comment|/**    * Retrieve the socket address that is used by OM.    * @param conf    * @return Target InetSocketAddress for the SCM service endpoint.    */
DECL|method|getOmAddress (Configuration conf)
specifier|public
specifier|static
name|InetSocketAddress
name|getOmAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|getOmRpcAddress
argument_list|(
name|conf
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Retrieve the socket address that is used by OM.    * @param conf    * @return Target InetSocketAddress for the SCM service endpoint.    */
DECL|method|getOmRpcAddress (Configuration conf)
specifier|public
specifier|static
name|String
name|getOmRpcAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|host
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|OZONE_OM_ADDRESS_KEY
argument_list|)
decl_stmt|;
return|return
name|host
operator|.
name|orElse
argument_list|(
name|OZONE_OM_BIND_HOST_DEFAULT
argument_list|)
operator|+
literal|":"
operator|+
name|getOmRpcPort
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Retrieve the socket address that is used by OM as specified by the confKey.    * Return null if the specified conf key is not set.    * @param conf configuration    * @param confKey configuration key to lookup address from    * @return Target InetSocketAddress for the OM RPC server.    */
DECL|method|getOmRpcAddress (Configuration conf, String confKey)
specifier|public
specifier|static
name|String
name|getOmRpcAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|confKey
parameter_list|)
block|{
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|host
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|confKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|host
operator|.
name|get
argument_list|()
operator|+
literal|":"
operator|+
name|getOmRpcPort
argument_list|(
name|conf
argument_list|,
name|confKey
argument_list|)
return|;
block|}
else|else
block|{
comment|// The specified confKey is not set
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Retrieve the socket address that should be used by clients to connect    * to OM.    * @param conf    * @return Target InetSocketAddress for the OM service endpoint.    */
DECL|method|getOmAddressForClients ( Configuration conf)
specifier|public
specifier|static
name|InetSocketAddress
name|getOmAddressForClients
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|host
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|OZONE_OM_ADDRESS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|host
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|OZONE_OM_ADDRESS_KEY
operator|+
literal|" must be defined. See"
operator|+
literal|" https://wiki.apache.org/hadoop/Ozone#Configuration for"
operator|+
literal|" details on configuring Ozone."
argument_list|)
throw|;
block|}
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|host
operator|.
name|get
argument_list|()
operator|+
literal|":"
operator|+
name|getOmRpcPort
argument_list|(
name|conf
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getOmRpcPort (Configuration conf)
specifier|public
specifier|static
name|int
name|getOmRpcPort
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// If no port number is specified then we'll just try the defaultBindPort.
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|port
init|=
name|getPortNumberFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|OZONE_OM_ADDRESS_KEY
argument_list|)
decl_stmt|;
return|return
name|port
operator|.
name|orElse
argument_list|(
name|OZONE_OM_PORT_DEFAULT
argument_list|)
return|;
block|}
comment|/**    * Retrieve the port that is used by OM as specified by the confKey.    * Return default port if port is not specified in the confKey.    * @param conf configuration    * @param confKey configuration key to lookup address from    * @return Port on which OM RPC server will listen on    */
DECL|method|getOmRpcPort (Configuration conf, String confKey)
specifier|public
specifier|static
name|int
name|getOmRpcPort
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|confKey
parameter_list|)
block|{
comment|// If no port number is specified then we'll just try the defaultBindPort.
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|port
init|=
name|getPortNumberFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|confKey
argument_list|)
decl_stmt|;
return|return
name|port
operator|.
name|orElse
argument_list|(
name|OZONE_OM_PORT_DEFAULT
argument_list|)
return|;
block|}
DECL|method|getOmRestPort (Configuration conf)
specifier|public
specifier|static
name|int
name|getOmRestPort
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// If no port number is specified then we'll just try the default
comment|// HTTP BindPort.
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|port
init|=
name|getPortNumberFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|OZONE_OM_HTTP_ADDRESS_KEY
argument_list|)
decl_stmt|;
return|return
name|port
operator|.
name|orElse
argument_list|(
name|OZONE_OM_HTTP_BIND_PORT_DEFAULT
argument_list|)
return|;
block|}
comment|/**    * Get the location where OM should store its metadata directories.    * Fall back to OZONE_METADATA_DIRS if not defined.    *    * @param conf - Config    * @return File path, after creating all the required Directories.    */
DECL|method|getOmDbDir (Configuration conf)
specifier|public
specifier|static
name|File
name|getOmDbDir
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|ServerUtils
operator|.
name|getDBPath
argument_list|(
name|conf
argument_list|,
name|OMConfigKeys
operator|.
name|OZONE_OM_DB_DIRS
argument_list|)
return|;
block|}
comment|/**    * Checks if the OM request is read only or not.    * @param omRequest OMRequest proto    * @return True if its readOnly, false otherwise.    */
DECL|method|isReadOnly ( OzoneManagerProtocolProtos.OMRequest omRequest)
specifier|public
specifier|static
name|boolean
name|isReadOnly
parameter_list|(
name|OzoneManagerProtocolProtos
operator|.
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|OzoneManagerProtocolProtos
operator|.
name|Type
name|cmdType
init|=
name|omRequest
operator|.
name|getCmdType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|cmdType
condition|)
block|{
case|case
name|CheckVolumeAccess
case|:
case|case
name|InfoVolume
case|:
case|case
name|ListVolume
case|:
case|case
name|InfoBucket
case|:
case|case
name|ListBuckets
case|:
case|case
name|LookupKey
case|:
case|case
name|ListKeys
case|:
case|case
name|InfoS3Bucket
case|:
case|case
name|ListS3Buckets
case|:
case|case
name|ServiceList
case|:
case|case
name|ListMultiPartUploadParts
case|:
case|case
name|GetFileStatus
case|:
case|case
name|LookupFile
case|:
case|case
name|ListStatus
case|:
case|case
name|GetAcl
case|:
return|return
literal|true
return|;
case|case
name|CreateVolume
case|:
case|case
name|SetVolumeProperty
case|:
case|case
name|DeleteVolume
case|:
case|case
name|CreateBucket
case|:
case|case
name|SetBucketProperty
case|:
case|case
name|DeleteBucket
case|:
case|case
name|CreateKey
case|:
case|case
name|RenameKey
case|:
case|case
name|DeleteKey
case|:
case|case
name|CommitKey
case|:
case|case
name|AllocateBlock
case|:
case|case
name|CreateS3Bucket
case|:
case|case
name|DeleteS3Bucket
case|:
case|case
name|InitiateMultiPartUpload
case|:
case|case
name|CommitMultiPartUpload
case|:
case|case
name|CompleteMultiPartUpload
case|:
case|case
name|AbortMultiPartUpload
case|:
case|case
name|GetS3Secret
case|:
case|case
name|GetDelegationToken
case|:
case|case
name|RenewDelegationToken
case|:
case|case
name|CancelDelegationToken
case|:
case|case
name|ApplyCreateKey
case|:
case|case
name|ApplyInitiateMultiPartUpload
case|:
case|case
name|CreateDirectory
case|:
case|case
name|CreateFile
case|:
case|case
name|RemoveAcl
case|:
case|case
name|SetAcl
case|:
case|case
name|AddAcl
case|:
return|return
literal|false
return|;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"CmdType {} is not categorized as readOnly or not."
argument_list|,
name|cmdType
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
DECL|method|getMD5Digest (String input)
specifier|public
specifier|static
name|byte
index|[]
name|getMD5Digest
parameter_list|(
name|String
name|input
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|MessageDigest
name|md
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|OzoneConsts
operator|.
name|MD5_HASH
argument_list|)
decl_stmt|;
return|return
name|md
operator|.
name|digest
argument_list|(
name|input
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error creating an instance of MD5 digest.\n"
operator|+
literal|"This could possibly indicate a faulty JRE"
argument_list|)
throw|;
block|}
block|}
DECL|method|getSHADigest ()
specifier|public
specifier|static
name|byte
index|[]
name|getSHADigest
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|MessageDigest
name|sha
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|OzoneConsts
operator|.
name|FILE_HASH
argument_list|)
decl_stmt|;
return|return
name|sha
operator|.
name|digest
argument_list|(
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|32
argument_list|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error creating an instance of SHA-256 digest.\n"
operator|+
literal|"This could possibly indicate a faulty JRE"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Add non empty and non null suffix to a key.    */
DECL|method|addSuffix (String key, String suffix)
specifier|private
specifier|static
name|String
name|addSuffix
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
if|if
condition|(
name|suffix
operator|==
literal|null
operator|||
name|suffix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|key
return|;
block|}
assert|assert
operator|!
name|suffix
operator|.
name|startsWith
argument_list|(
literal|"."
argument_list|)
operator|:
literal|"suffix '"
operator|+
name|suffix
operator|+
literal|"' should not already have '.' prepended."
assert|;
return|return
name|key
operator|+
literal|"."
operator|+
name|suffix
return|;
block|}
comment|/**    * Concatenate list of suffix strings '.' separated.    */
DECL|method|concatSuffixes (String... suffixes)
specifier|private
specifier|static
name|String
name|concatSuffixes
parameter_list|(
name|String
modifier|...
name|suffixes
parameter_list|)
block|{
if|if
condition|(
name|suffixes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|"."
argument_list|)
operator|.
name|skipNulls
argument_list|()
operator|.
name|join
argument_list|(
name|suffixes
argument_list|)
return|;
block|}
comment|/**    * Return configuration key of format key.suffix1.suffix2...suffixN.    */
DECL|method|addKeySuffixes (String key, String... suffixes)
specifier|public
specifier|static
name|String
name|addKeySuffixes
parameter_list|(
name|String
name|key
parameter_list|,
name|String
modifier|...
name|suffixes
parameter_list|)
block|{
name|String
name|keySuffix
init|=
name|concatSuffixes
argument_list|(
name|suffixes
argument_list|)
decl_stmt|;
return|return
name|addSuffix
argument_list|(
name|key
argument_list|,
name|keySuffix
argument_list|)
return|;
block|}
comment|/**    * Match input address to local address.    * Return true if it matches, false otherwsie.    */
DECL|method|isAddressLocal (InetSocketAddress addr)
specifier|public
specifier|static
name|boolean
name|isAddressLocal
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|)
block|{
return|return
name|NetUtils
operator|.
name|isLocalAddress
argument_list|(
name|addr
operator|.
name|getAddress
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get a collection of all omNodeIds for the given omServiceId.    */
DECL|method|getOMNodeIds (Configuration conf, String omServiceId)
specifier|public
specifier|static
name|Collection
argument_list|<
name|String
argument_list|>
name|getOMNodeIds
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|omServiceId
parameter_list|)
block|{
name|String
name|key
init|=
name|addSuffix
argument_list|(
name|OZONE_OM_NODES_KEY
argument_list|,
name|omServiceId
argument_list|)
decl_stmt|;
return|return
name|conf
operator|.
name|getTrimmedStringCollection
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * @return<code>coll</code> if it is non-null and non-empty. Otherwise,    * returns a list with a single null value.    */
DECL|method|emptyAsSingletonNull (Collection<String> coll)
specifier|public
specifier|static
name|Collection
argument_list|<
name|String
argument_list|>
name|emptyAsSingletonNull
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|coll
parameter_list|)
block|{
if|if
condition|(
name|coll
operator|==
literal|null
operator|||
name|coll
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|coll
return|;
block|}
block|}
comment|/**    * Given a source directory, create a tar.gz file from it.    *    * @param sourcePath the path to the directory to be archived.    * @return tar.gz file    * @throws IOException    */
DECL|method|createTarFile (Path sourcePath)
specifier|public
specifier|static
name|File
name|createTarFile
parameter_list|(
name|Path
name|sourcePath
parameter_list|)
throws|throws
name|IOException
block|{
name|TarArchiveOutputStream
name|tarOs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|sourceDir
init|=
name|sourcePath
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|sourceDir
operator|.
name|concat
argument_list|(
literal|".tar.gz"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fileOutputStream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|GZIPOutputStream
name|gzipOutputStream
init|=
operator|new
name|GZIPOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|fileOutputStream
argument_list|)
argument_list|)
decl_stmt|;
name|tarOs
operator|=
operator|new
name|TarArchiveOutputStream
argument_list|(
name|gzipOutputStream
argument_list|)
expr_stmt|;
name|File
name|folder
init|=
operator|new
name|File
argument_list|(
name|sourceDir
argument_list|)
decl_stmt|;
name|File
index|[]
name|filesInDir
init|=
name|folder
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|filesInDir
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|file
range|:
name|filesInDir
control|)
block|{
name|addFilesToArchive
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|,
name|file
argument_list|,
name|tarOs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
return|;
block|}
finally|finally
block|{
try|try
block|{
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|tarOs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception encountered when closing "
operator|+
literal|"TAR file output stream: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addFilesToArchive (String source, File file, TarArchiveOutputStream tarFileOutputStream)
specifier|private
specifier|static
name|void
name|addFilesToArchive
parameter_list|(
name|String
name|source
parameter_list|,
name|File
name|file
parameter_list|,
name|TarArchiveOutputStream
name|tarFileOutputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|tarFileOutputStream
operator|.
name|putArchiveEntry
argument_list|(
operator|new
name|TarArchiveEntry
argument_list|(
name|file
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|FileInputStream
name|fileInputStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|BufferedInputStream
name|bufferedInputStream
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|fileInputStream
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|bufferedInputStream
argument_list|,
name|tarFileOutputStream
argument_list|)
expr_stmt|;
name|tarFileOutputStream
operator|.
name|closeArchiveEntry
argument_list|()
expr_stmt|;
name|fileInputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|tarFileOutputStream
operator|.
name|closeArchiveEntry
argument_list|()
expr_stmt|;
name|File
index|[]
name|filesInDir
init|=
name|file
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|filesInDir
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|cFile
range|:
name|filesInDir
control|)
block|{
name|addFilesToArchive
argument_list|(
name|cFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|cFile
argument_list|,
name|tarFileOutputStream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * If a OM conf is only set with key suffixed with OM Node ID, return the    * set value.    * @return null if base conf key is set, otherwise the value set for    * key suffixed with Node ID.    */
DECL|method|getConfSuffixedWithOMNodeId (Configuration conf, String confKey, String omServiceID, String omNodeId)
specifier|public
specifier|static
name|String
name|getConfSuffixedWithOMNodeId
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|confKey
parameter_list|,
name|String
name|omServiceID
parameter_list|,
name|String
name|omNodeId
parameter_list|)
block|{
name|String
name|confValue
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|confKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|confValue
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|suffixedConfKey
init|=
name|OmUtils
operator|.
name|addKeySuffixes
argument_list|(
name|confKey
argument_list|,
name|omServiceID
argument_list|,
name|omNodeId
argument_list|)
decl_stmt|;
name|confValue
operator|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|suffixedConfKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|confValue
argument_list|)
condition|)
block|{
return|return
name|confValue
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Returns the http address of peer OM node.    * @param conf Configuration    * @param omNodeId peer OM node ID    * @param omNodeHostAddr peer OM node host address    * @return http address of peer OM node in the format<hostName>:<port>    */
DECL|method|getHttpAddressForOMPeerNode (Configuration conf, String omServiceId, String omNodeId, String omNodeHostAddr)
specifier|public
specifier|static
name|String
name|getHttpAddressForOMPeerNode
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|omServiceId
parameter_list|,
name|String
name|omNodeId
parameter_list|,
name|String
name|omNodeHostAddr
parameter_list|)
block|{
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|bindHost
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|addKeySuffixes
argument_list|(
name|OZONE_OM_HTTP_BIND_HOST_KEY
argument_list|,
name|omServiceId
argument_list|,
name|omNodeId
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|addressPort
init|=
name|getPortNumberFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|addKeySuffixes
argument_list|(
name|OZONE_OM_HTTP_ADDRESS_KEY
argument_list|,
name|omServiceId
argument_list|,
name|omNodeId
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|addressHost
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|addKeySuffixes
argument_list|(
name|OZONE_OM_HTTP_ADDRESS_KEY
argument_list|,
name|omServiceId
argument_list|,
name|omNodeId
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|hostName
init|=
name|bindHost
operator|.
name|orElse
argument_list|(
name|addressHost
operator|.
name|orElse
argument_list|(
name|omNodeHostAddr
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|hostName
operator|+
literal|":"
operator|+
name|addressPort
operator|.
name|orElse
argument_list|(
name|OZONE_OM_HTTP_BIND_PORT_DEFAULT
argument_list|)
return|;
block|}
comment|/**    * Returns the https address of peer OM node.    * @param conf Configuration    * @param omNodeId peer OM node ID    * @param omNodeHostAddr peer OM node host address    * @return https address of peer OM node in the format<hostName>:<port>    */
DECL|method|getHttpsAddressForOMPeerNode (Configuration conf, String omServiceId, String omNodeId, String omNodeHostAddr)
specifier|public
specifier|static
name|String
name|getHttpsAddressForOMPeerNode
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|omServiceId
parameter_list|,
name|String
name|omNodeId
parameter_list|,
name|String
name|omNodeHostAddr
parameter_list|)
block|{
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|bindHost
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|addKeySuffixes
argument_list|(
name|OZONE_OM_HTTPS_BIND_HOST_KEY
argument_list|,
name|omServiceId
argument_list|,
name|omNodeId
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|addressPort
init|=
name|getPortNumberFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|addKeySuffixes
argument_list|(
name|OZONE_OM_HTTPS_ADDRESS_KEY
argument_list|,
name|omServiceId
argument_list|,
name|omNodeId
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|addressHost
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|addKeySuffixes
argument_list|(
name|OZONE_OM_HTTPS_ADDRESS_KEY
argument_list|,
name|omServiceId
argument_list|,
name|omNodeId
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|hostName
init|=
name|bindHost
operator|.
name|orElse
argument_list|(
name|addressHost
operator|.
name|orElse
argument_list|(
name|omNodeHostAddr
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|hostName
operator|+
literal|":"
operator|+
name|addressPort
operator|.
name|orElse
argument_list|(
name|OZONE_OM_HTTPS_BIND_PORT_DEFAULT
argument_list|)
return|;
block|}
comment|/**    * Get the local directory where ratis logs will be stored.    */
DECL|method|getOMRatisDirectory (Configuration conf)
specifier|public
specifier|static
name|String
name|getOMRatisDirectory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|storageDir
init|=
name|conf
operator|.
name|get
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_RATIS_STORAGE_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|storageDir
argument_list|)
condition|)
block|{
name|storageDir
operator|=
name|HddsServerUtil
operator|.
name|getDefaultRatisDirectory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
name|storageDir
return|;
block|}
DECL|method|getOMRatisSnapshotDirectory (Configuration conf)
specifier|public
specifier|static
name|String
name|getOMRatisSnapshotDirectory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|snapshotDir
init|=
name|conf
operator|.
name|get
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_RATIS_SNAPSHOT_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|snapshotDir
argument_list|)
condition|)
block|{
name|snapshotDir
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|getOMRatisDirectory
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|"snapshot"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|snapshotDir
return|;
block|}
DECL|method|createOMDir (String dirPath)
specifier|public
specifier|static
name|File
name|createOMDir
parameter_list|(
name|String
name|dirPath
parameter_list|)
block|{
name|File
name|dirFile
init|=
operator|new
name|File
argument_list|(
name|dirPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dirFile
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|dirFile
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to create path: "
operator|+
name|dirFile
argument_list|)
throw|;
block|}
return|return
name|dirFile
return|;
block|}
block|}
end_class

end_unit

