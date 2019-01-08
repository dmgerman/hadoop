begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3native
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3native
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|annotations
operator|.
name|VisibleForTesting
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
name|Preconditions
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
operator|.
name|equalsIgnoreCase
import|;
end_import

begin_comment
comment|/**  * Class to aid logging in to S3 endpoints.  * It is in S3N so that it can be used across all S3 filesystems.  *  * The core function of this class was the extraction and decoding of user:secret  * information from filesystems URIs. As this is no longer supported,  * its role has been reduced to checking for secrets in the URI and rejecting  * them where found.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|S3xLoginHelper
specifier|public
specifier|final
class|class
name|S3xLoginHelper
block|{
DECL|method|S3xLoginHelper ()
specifier|private
name|S3xLoginHelper
parameter_list|()
block|{   }
DECL|field|LOGIN_WARNING
specifier|public
specifier|static
specifier|final
name|String
name|LOGIN_WARNING
init|=
literal|"The Filesystem URI contains login details."
operator|+
literal|" This authentication mechanism is no longer supported."
decl_stmt|;
comment|/**    * Build the filesystem URI.    * @param uri filesystem uri    * @return the URI to use as the basis for FS operation and qualifying paths.    * @throws IllegalArgumentException if the URI is in some way invalid.    */
DECL|method|buildFSURI (URI uri)
specifier|public
specifier|static
name|URI
name|buildFSURI
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
comment|// look for login secrets and fail if they are present.
name|rejectSecretsInURIs
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|uri
argument_list|,
literal|"null uri"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|,
literal|"null uri.getScheme()"
argument_list|)
expr_stmt|;
if|if
condition|(
name|uri
operator|.
name|getHost
argument_list|()
operator|==
literal|null
operator|&&
name|uri
operator|.
name|getAuthority
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|,
literal|"null uri host."
argument_list|)
expr_stmt|;
block|}
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|,
literal|"null uri host."
argument_list|)
expr_stmt|;
return|return
name|URI
operator|.
name|create
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
operator|+
literal|"://"
operator|+
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a stripped down string value for error messages.    * @param pathUri URI    * @return a shortened schema://host/path value    */
DECL|method|toString (URI pathUri)
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|URI
name|pathUri
parameter_list|)
block|{
return|return
name|pathUri
operator|!=
literal|null
condition|?
name|String
operator|.
name|format
argument_list|(
literal|"%s://%s/%s"
argument_list|,
name|pathUri
operator|.
name|getScheme
argument_list|()
argument_list|,
name|pathUri
operator|.
name|getHost
argument_list|()
argument_list|,
name|pathUri
operator|.
name|getPath
argument_list|()
argument_list|)
else|:
literal|"(null URI)"
return|;
block|}
comment|/**    * Extract the login details from a URI, raising an exception if    * the URI contains them.    * @param name URI of the filesystem, can be null    * @throws IllegalArgumentException if there is a secret in the URI.    */
DECL|method|rejectSecretsInURIs (URI name)
specifier|public
specifier|static
name|void
name|rejectSecretsInURIs
parameter_list|(
name|URI
name|name
parameter_list|)
block|{
name|Login
name|login
init|=
name|extractLoginDetails
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|login
operator|.
name|hasLogin
argument_list|()
argument_list|,
name|LOGIN_WARNING
argument_list|)
expr_stmt|;
block|}
comment|/**    * Extract the login details from a URI.    * @param name URI of the filesystem, may be null    * @return a login tuple, possibly empty.    */
annotation|@
name|VisibleForTesting
DECL|method|extractLoginDetails (URI name)
specifier|static
name|Login
name|extractLoginDetails
parameter_list|(
name|URI
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
name|Login
operator|.
name|EMPTY
return|;
block|}
name|String
name|authority
init|=
name|name
operator|.
name|getAuthority
argument_list|()
decl_stmt|;
if|if
condition|(
name|authority
operator|==
literal|null
condition|)
block|{
return|return
name|Login
operator|.
name|EMPTY
return|;
block|}
name|int
name|loginIndex
init|=
name|authority
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
if|if
condition|(
name|loginIndex
operator|<
literal|0
condition|)
block|{
comment|// no login
return|return
name|Login
operator|.
name|EMPTY
return|;
block|}
name|String
name|login
init|=
name|authority
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|loginIndex
argument_list|)
decl_stmt|;
name|int
name|loginSplit
init|=
name|login
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|loginSplit
operator|>
literal|0
condition|)
block|{
name|String
name|user
init|=
name|login
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|loginSplit
argument_list|)
decl_stmt|;
name|String
name|encodedPassword
init|=
name|login
operator|.
name|substring
argument_list|(
name|loginSplit
operator|+
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|Login
argument_list|(
name|user
argument_list|,
name|encodedPassword
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
literal|"password removed"
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|loginSplit
operator|==
literal|0
condition|)
block|{
comment|// there is no user, just a password. In this case, there's no login
return|return
name|Login
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
comment|// loginSplit< 0: there is no ":".
comment|// return a login with a null password
return|return
operator|new
name|Login
argument_list|(
name|login
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
comment|/**    * Canonicalize the given URI.    *    * This strips out login information.    *    * @param uri the URI to canonicalize    * @param defaultPort default port to use in canonicalized URI if the input    *     URI has no port and this value is greater than 0    * @return a new, canonicalized URI.    */
DECL|method|canonicalizeUri (URI uri, int defaultPort)
specifier|public
specifier|static
name|URI
name|canonicalizeUri
parameter_list|(
name|URI
name|uri
parameter_list|,
name|int
name|defaultPort
parameter_list|)
block|{
if|if
condition|(
name|uri
operator|.
name|getPort
argument_list|()
operator|==
operator|-
literal|1
operator|&&
name|defaultPort
operator|>
literal|0
condition|)
block|{
comment|// reconstruct the uri with the default port set
try|try
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|,
name|uri
operator|.
name|getUserInfo
argument_list|()
argument_list|,
name|uri
operator|.
name|getHost
argument_list|()
argument_list|,
name|defaultPort
argument_list|,
name|uri
operator|.
name|getPath
argument_list|()
argument_list|,
name|uri
operator|.
name|getQuery
argument_list|()
argument_list|,
name|uri
operator|.
name|getFragment
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
comment|// Should never happen!
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Valid URI became unparseable: "
operator|+
name|uri
argument_list|)
throw|;
block|}
block|}
return|return
name|uri
return|;
block|}
comment|/**    * Check the path, ignoring authentication details.    * See {@link FileSystem#checkPath(Path)} for the operation of this.    *    * Essentially    *<ol>    *<li>The URI is canonicalized.</li>    *<li>If the schemas match, the hosts are compared.</li>    *<li>If there is a mismatch between null/non-null host, the default FS    *   values are used to patch in the host.</li>    *</ol>    * That all originates in the core FS; the sole change here being to use    * {@link URI#getHost()} over {@link URI#getAuthority()}. Some of that    * code looks a relic of the code anti-pattern of using "hdfs:file.txt"    * to define the path without declaring the hostname. It's retained    * for compatibility.    * @param conf FS configuration    * @param fsUri the FS URI    * @param path path to check    * @param defaultPort default port of FS    */
DECL|method|checkPath (Configuration conf, URI fsUri, Path path, int defaultPort)
specifier|public
specifier|static
name|void
name|checkPath
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|fsUri
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|defaultPort
parameter_list|)
block|{
name|URI
name|pathUri
init|=
name|path
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|String
name|thatScheme
init|=
name|pathUri
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
name|thatScheme
operator|==
literal|null
condition|)
block|{
comment|// fs is relative
return|return;
block|}
name|URI
name|thisUri
init|=
name|canonicalizeUri
argument_list|(
name|fsUri
argument_list|,
name|defaultPort
argument_list|)
decl_stmt|;
name|String
name|thisScheme
init|=
name|thisUri
operator|.
name|getScheme
argument_list|()
decl_stmt|;
comment|//hostname and scheme are not case sensitive in these checks
if|if
condition|(
name|equalsIgnoreCase
argument_list|(
name|thisScheme
argument_list|,
name|thatScheme
argument_list|)
condition|)
block|{
comment|// schemes match
name|String
name|thisHost
init|=
name|thisUri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|String
name|thatHost
init|=
name|pathUri
operator|.
name|getHost
argument_list|()
decl_stmt|;
if|if
condition|(
name|thatHost
operator|==
literal|null
operator|&&
comment|// path's host is null
name|thisHost
operator|!=
literal|null
condition|)
block|{
comment|// fs has a host
name|URI
name|defaultUri
init|=
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|equalsIgnoreCase
argument_list|(
name|thisScheme
argument_list|,
name|defaultUri
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
name|pathUri
operator|=
name|defaultUri
expr_stmt|;
comment|// schemes match, so use this uri instead
block|}
else|else
block|{
name|pathUri
operator|=
literal|null
expr_stmt|;
comment|// can't determine auth of the path
block|}
block|}
if|if
condition|(
name|pathUri
operator|!=
literal|null
condition|)
block|{
comment|// canonicalize uri before comparing with this fs
name|pathUri
operator|=
name|canonicalizeUri
argument_list|(
name|pathUri
argument_list|,
name|defaultPort
argument_list|)
expr_stmt|;
name|thatHost
operator|=
name|pathUri
operator|.
name|getHost
argument_list|()
expr_stmt|;
if|if
condition|(
name|thisHost
operator|==
name|thatHost
operator|||
comment|// hosts match
operator|(
name|thisHost
operator|!=
literal|null
operator|&&
name|equalsIgnoreCase
argument_list|(
name|thisHost
argument_list|,
name|thatHost
argument_list|)
operator|)
condition|)
block|{
return|return;
block|}
block|}
block|}
comment|// make sure the exception strips out any auth details
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Wrong FS "
operator|+
name|S3xLoginHelper
operator|.
name|toString
argument_list|(
name|pathUri
argument_list|)
operator|+
literal|" -expected "
operator|+
name|fsUri
argument_list|)
throw|;
block|}
comment|/**    * Simple tuple of login details.    */
DECL|class|Login
specifier|public
specifier|static
class|class
name|Login
block|{
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|password
specifier|private
specifier|final
name|String
name|password
decl_stmt|;
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|Login
name|EMPTY
init|=
operator|new
name|Login
argument_list|()
decl_stmt|;
comment|/**      * Create an instance with no login details.      * Calls to {@link #hasLogin()} return false.      */
DECL|method|Login ()
specifier|public
name|Login
parameter_list|()
block|{
name|this
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|Login (String user, String password)
specifier|public
name|Login
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
comment|/**      * Predicate to verify login details are defined.      * @return true if the instance contains login information.      */
DECL|method|hasLogin ()
specifier|public
name|boolean
name|hasLogin
parameter_list|()
block|{
return|return
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|password
argument_list|)
operator|||
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|user
argument_list|)
return|;
block|}
comment|/**      * Equality test matches user and password.      * @param o other object      * @return true if the objects are considered equivalent.      */
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
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Login
name|that
init|=
operator|(
name|Login
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|user
argument_list|,
name|that
operator|.
name|user
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|password
argument_list|,
name|that
operator|.
name|password
argument_list|)
return|;
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
name|Objects
operator|.
name|hash
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|getPassword ()
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
block|}
block|}
end_class

end_unit

