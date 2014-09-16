begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
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
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|login
operator|.
name|AppConfigurationEntry
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
name|login
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|RetryPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|CuratorFramework
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|CuratorFrameworkFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|api
operator|.
name|ACLProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|imps
operator|.
name|DefaultACLProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|retry
operator|.
name|ExponentialBackoffRetry
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
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooDefs
operator|.
name|Perms
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|client
operator|.
name|ZooKeeperSaslClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Id
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
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
comment|/**  * A SignerSecretProvider that synchronizes a rolling random secret between  * multiple servers using ZooKeeper.  *<p/>  * It works by storing the secrets and next rollover time in a ZooKeeper znode.  * All ZKSignerSecretProviders looking at that znode will use those  * secrets and next rollover time to ensure they are synchronized.  There is no  * "leader" -- any of the ZKSignerSecretProviders can choose the next secret;  * which one is indeterminate.  Kerberos-based ACLs can also be enforced to  * prevent a malicious third-party from getting or setting the secrets.  It uses  * its own CuratorFramework client for talking to ZooKeeper.  If you want to use  * your own Curator client, you can pass it to ZKSignerSecretProvider; see  * {@link org.apache.hadoop.security.authentication.server.AuthenticationFilter}  * for more details.  *<p/>  * The supported configuration properties are:  *<ul>  *<li>signer.secret.provider.zookeeper.connection.string: indicates the  * ZooKeeper connection string to connect with.</li>  *<li>signer.secret.provider.zookeeper.path: indicates the ZooKeeper path  * to use for storing and retrieving the secrets.  All ZKSignerSecretProviders  * that need to coordinate should point to the same path.</li>  *<li>signer.secret.provider.zookeeper.auth.type: indicates the auth type to  * use.  Supported values are "none" and "sasl".  The default value is "none"  *</li>  *<li>signer.secret.provider.zookeeper.kerberos.keytab: set this to the path  * with the Kerberos keytab file.  This is only required if using Kerberos.</li>  *<li>signer.secret.provider.zookeeper.kerberos.principal: set this to the  * Kerberos principal to use.  This only required if using Kerberos.</li>  *<li>signer.secret.provider.zookeeper.disconnect.on.close: when set to "true",  * ZKSignerSecretProvider will close the ZooKeeper connection on shutdown.  The  * default is "true". Only set this to "false" if a custom Curator client is  * being provided and the disconnection is being handled elsewhere.</li>  *</ul>  *  * The following attribute in the ServletContext can also be set if desired:  *<li>signer.secret.provider.zookeeper.curator.client: A CuratorFramework  * client object can be passed here. If given, the "zookeeper" implementation  * will use this Curator client instead of creating its own, which is useful if  * you already have a Curator client or want more control over its  * configuration.</li>  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ZKSignerSecretProvider
specifier|public
class|class
name|ZKSignerSecretProvider
extends|extends
name|RolloverSignerSecretProvider
block|{
DECL|field|CONFIG_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|CONFIG_PREFIX
init|=
literal|"signer.secret.provider.zookeeper."
decl_stmt|;
comment|/**    * Constant for the property that specifies the ZooKeeper connection string.    */
DECL|field|ZOOKEEPER_CONNECTION_STRING
specifier|public
specifier|static
specifier|final
name|String
name|ZOOKEEPER_CONNECTION_STRING
init|=
name|CONFIG_PREFIX
operator|+
literal|"connection.string"
decl_stmt|;
comment|/**    * Constant for the property that specifies the ZooKeeper path.    */
DECL|field|ZOOKEEPER_PATH
specifier|public
specifier|static
specifier|final
name|String
name|ZOOKEEPER_PATH
init|=
name|CONFIG_PREFIX
operator|+
literal|"path"
decl_stmt|;
comment|/**    * Constant for the property that specifies the auth type to use.  Supported    * values are "none" and "sasl".  The default value is "none".    */
DECL|field|ZOOKEEPER_AUTH_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|ZOOKEEPER_AUTH_TYPE
init|=
name|CONFIG_PREFIX
operator|+
literal|"auth.type"
decl_stmt|;
comment|/**    * Constant for the property that specifies the Kerberos keytab file.    */
DECL|field|ZOOKEEPER_KERBEROS_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|ZOOKEEPER_KERBEROS_KEYTAB
init|=
name|CONFIG_PREFIX
operator|+
literal|"kerberos.keytab"
decl_stmt|;
comment|/**    * Constant for the property that specifies the Kerberos principal.    */
DECL|field|ZOOKEEPER_KERBEROS_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|ZOOKEEPER_KERBEROS_PRINCIPAL
init|=
name|CONFIG_PREFIX
operator|+
literal|"kerberos.principal"
decl_stmt|;
comment|/**    * Constant for the property that specifies whether or not the Curator client    * should disconnect from ZooKeeper on shutdown.  The default is "true".  Only    * set this to "false" if a custom Curator client is being provided and the    * disconnection is being handled elsewhere.    */
DECL|field|DISCONNECT_FROM_ZOOKEEPER_ON_SHUTDOWN
specifier|public
specifier|static
specifier|final
name|String
name|DISCONNECT_FROM_ZOOKEEPER_ON_SHUTDOWN
init|=
name|CONFIG_PREFIX
operator|+
literal|"disconnect.on.shutdown"
decl_stmt|;
comment|/**    * Constant for the ServletContext attribute that can be used for providing a    * custom CuratorFramework client. If set ZKSignerSecretProvider will use this    * Curator client instead of creating a new one. The providing class is    * responsible for creating and configuring the Curator client (including    * security and ACLs) in this case.    */
specifier|public
specifier|static
specifier|final
name|String
DECL|field|ZOOKEEPER_SIGNER_SECRET_PROVIDER_CURATOR_CLIENT_ATTRIBUTE
name|ZOOKEEPER_SIGNER_SECRET_PROVIDER_CURATOR_CLIENT_ATTRIBUTE
init|=
name|CONFIG_PREFIX
operator|+
literal|"curator.client"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ZKSignerSecretProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
comment|/**    * Stores the next secret that will be used after the current one rolls over.    * We do this to help with rollover performance by actually deciding the next    * secret at the previous rollover.  This allows us to switch to the next    * secret very quickly.  Afterwards, we have plenty of time to decide on the    * next secret.    */
DECL|field|nextSecret
specifier|private
specifier|volatile
name|byte
index|[]
name|nextSecret
decl_stmt|;
DECL|field|rand
specifier|private
specifier|final
name|Random
name|rand
decl_stmt|;
comment|/**    * Stores the current version of the znode.    */
DECL|field|zkVersion
specifier|private
name|int
name|zkVersion
decl_stmt|;
comment|/**    * Stores the next date that the rollover will occur.  This is only used    * for allowing new servers joining later to synchronize their rollover    * with everyone else.    */
DECL|field|nextRolloverDate
specifier|private
name|long
name|nextRolloverDate
decl_stmt|;
DECL|field|tokenValidity
specifier|private
name|long
name|tokenValidity
decl_stmt|;
DECL|field|client
specifier|private
name|CuratorFramework
name|client
decl_stmt|;
DECL|field|shouldDisconnect
specifier|private
name|boolean
name|shouldDisconnect
decl_stmt|;
DECL|field|INT_BYTES
specifier|private
specifier|static
name|int
name|INT_BYTES
init|=
name|Integer
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
decl_stmt|;
DECL|field|LONG_BYTES
specifier|private
specifier|static
name|int
name|LONG_BYTES
init|=
name|Long
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
decl_stmt|;
DECL|field|DATA_VERSION
specifier|private
specifier|static
name|int
name|DATA_VERSION
init|=
literal|0
decl_stmt|;
DECL|method|ZKSignerSecretProvider ()
specifier|public
name|ZKSignerSecretProvider
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|rand
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
comment|/**    * This constructor lets you set the seed of the Random Number Generator and    * is meant for testing.    * @param seed the seed for the random number generator    */
annotation|@
name|VisibleForTesting
DECL|method|ZKSignerSecretProvider (long seed)
specifier|public
name|ZKSignerSecretProvider
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|rand
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Properties config, ServletContext servletContext, long tokenValidity)
specifier|public
name|void
name|init
parameter_list|(
name|Properties
name|config
parameter_list|,
name|ServletContext
name|servletContext
parameter_list|,
name|long
name|tokenValidity
parameter_list|)
throws|throws
name|Exception
block|{
name|Object
name|curatorClientObj
init|=
name|servletContext
operator|.
name|getAttribute
argument_list|(
name|ZOOKEEPER_SIGNER_SECRET_PROVIDER_CURATOR_CLIENT_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|curatorClientObj
operator|!=
literal|null
operator|&&
name|curatorClientObj
operator|instanceof
name|CuratorFramework
condition|)
block|{
name|client
operator|=
operator|(
name|CuratorFramework
operator|)
name|curatorClientObj
expr_stmt|;
block|}
else|else
block|{
name|client
operator|=
name|createCuratorClient
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|tokenValidity
operator|=
name|tokenValidity
expr_stmt|;
name|shouldDisconnect
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|config
operator|.
name|getProperty
argument_list|(
name|DISCONNECT_FROM_ZOOKEEPER_ON_SHUTDOWN
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|path
operator|=
name|config
operator|.
name|getProperty
argument_list|(
name|ZOOKEEPER_PATH
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ZOOKEEPER_PATH
operator|+
literal|" must be specified"
argument_list|)
throw|;
block|}
try|try
block|{
name|nextRolloverDate
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|tokenValidity
expr_stmt|;
comment|// everyone tries to do this, only one will succeed and only when the
comment|// znode doesn't already exist.  Everyone else will synchronize on the
comment|// data from the znode
name|client
operator|.
name|create
argument_list|()
operator|.
name|creatingParentsIfNeeded
argument_list|()
operator|.
name|forPath
argument_list|(
name|path
argument_list|,
name|generateZKData
argument_list|(
name|generateRandomSecret
argument_list|()
argument_list|,
name|generateRandomSecret
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|zkVersion
operator|=
literal|0
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating secret znode"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NodeExistsException
name|nee
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The secret znode already exists, retrieving data"
argument_list|)
expr_stmt|;
block|}
comment|// Synchronize on the data from the znode
comment|// passing true tells it to parse out all the data for initing
name|pullFromZK
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|long
name|initialDelay
init|=
name|nextRolloverDate
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// If it's in the past, try to find the next interval that we should
comment|// be using
if|if
condition|(
name|initialDelay
operator|<
literal|1l
condition|)
block|{
name|int
name|i
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|initialDelay
operator|<
literal|1l
condition|)
block|{
name|initialDelay
operator|=
name|nextRolloverDate
operator|+
name|tokenValidity
operator|*
name|i
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
name|super
operator|.
name|startScheduler
argument_list|(
name|initialDelay
argument_list|,
name|tokenValidity
argument_list|)
expr_stmt|;
block|}
comment|/**    * Disconnects from ZooKeeper unless told not to.    */
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
if|if
condition|(
name|shouldDisconnect
operator|&&
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rollSecret ()
specifier|protected
specifier|synchronized
name|void
name|rollSecret
parameter_list|()
block|{
name|super
operator|.
name|rollSecret
argument_list|()
expr_stmt|;
comment|// Try to push the information to ZooKeeper with a potential next secret.
name|nextRolloverDate
operator|+=
name|tokenValidity
expr_stmt|;
name|byte
index|[]
index|[]
name|secrets
init|=
name|super
operator|.
name|getAllSecrets
argument_list|()
decl_stmt|;
name|pushToZK
argument_list|(
name|generateRandomSecret
argument_list|()
argument_list|,
name|secrets
index|[
literal|0
index|]
argument_list|,
name|secrets
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// Pull info from ZooKeeper to get the decided next secret
comment|// passing false tells it that we don't care about most of the data
name|pullFromZK
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|generateNewSecret ()
specifier|protected
name|byte
index|[]
name|generateNewSecret
parameter_list|()
block|{
comment|// We simply return nextSecret because it's already been decided on
return|return
name|nextSecret
return|;
block|}
comment|/**    * Pushes proposed data to ZooKeeper.  If a different server pushes its data    * first, it gives up.    * @param newSecret The new secret to use    * @param currentSecret The current secret    * @param previousSecret  The previous secret    */
DECL|method|pushToZK (byte[] newSecret, byte[] currentSecret, byte[] previousSecret)
specifier|private
specifier|synchronized
name|void
name|pushToZK
parameter_list|(
name|byte
index|[]
name|newSecret
parameter_list|,
name|byte
index|[]
name|currentSecret
parameter_list|,
name|byte
index|[]
name|previousSecret
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
name|generateZKData
argument_list|(
name|newSecret
argument_list|,
name|currentSecret
argument_list|,
name|previousSecret
argument_list|)
decl_stmt|;
try|try
block|{
name|client
operator|.
name|setData
argument_list|()
operator|.
name|withVersion
argument_list|(
name|zkVersion
argument_list|)
operator|.
name|forPath
argument_list|(
name|path
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|BadVersionException
name|bve
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unable to push to znode; another server already did it"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"An unexpected exception occured pushing data to ZooKeeper"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Serialize the data to attempt to push into ZooKeeper.  The format is this:    *<p>    * [DATA_VERSION, newSecretLength, newSecret, currentSecretLength, currentSecret, previousSecretLength, previousSecret, nextRolloverDate]    *<p>    * Only previousSecret can be null, in which case the format looks like this:    *<p>    * [DATA_VERSION, newSecretLength, newSecret, currentSecretLength, currentSecret, 0, nextRolloverDate]    *<p>    * @param newSecret The new secret to use    * @param currentSecret The current secret    * @param previousSecret The previous secret    * @return The serialized data for ZooKeeper    */
DECL|method|generateZKData (byte[] newSecret, byte[] currentSecret, byte[] previousSecret)
specifier|private
specifier|synchronized
name|byte
index|[]
name|generateZKData
parameter_list|(
name|byte
index|[]
name|newSecret
parameter_list|,
name|byte
index|[]
name|currentSecret
parameter_list|,
name|byte
index|[]
name|previousSecret
parameter_list|)
block|{
name|int
name|newSecretLength
init|=
name|newSecret
operator|.
name|length
decl_stmt|;
name|int
name|currentSecretLength
init|=
name|currentSecret
operator|.
name|length
decl_stmt|;
name|int
name|previousSecretLength
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|previousSecret
operator|!=
literal|null
condition|)
block|{
name|previousSecretLength
operator|=
name|previousSecret
operator|.
name|length
expr_stmt|;
block|}
name|ByteBuffer
name|bb
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|INT_BYTES
operator|+
name|INT_BYTES
operator|+
name|newSecretLength
operator|+
name|INT_BYTES
operator|+
name|currentSecretLength
operator|+
name|INT_BYTES
operator|+
name|previousSecretLength
operator|+
name|LONG_BYTES
argument_list|)
decl_stmt|;
name|bb
operator|.
name|putInt
argument_list|(
name|DATA_VERSION
argument_list|)
expr_stmt|;
name|bb
operator|.
name|putInt
argument_list|(
name|newSecretLength
argument_list|)
expr_stmt|;
name|bb
operator|.
name|put
argument_list|(
name|newSecret
argument_list|)
expr_stmt|;
name|bb
operator|.
name|putInt
argument_list|(
name|currentSecretLength
argument_list|)
expr_stmt|;
name|bb
operator|.
name|put
argument_list|(
name|currentSecret
argument_list|)
expr_stmt|;
name|bb
operator|.
name|putInt
argument_list|(
name|previousSecretLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|previousSecretLength
operator|>
literal|0
condition|)
block|{
name|bb
operator|.
name|put
argument_list|(
name|previousSecret
argument_list|)
expr_stmt|;
block|}
name|bb
operator|.
name|putLong
argument_list|(
name|nextRolloverDate
argument_list|)
expr_stmt|;
return|return
name|bb
operator|.
name|array
argument_list|()
return|;
block|}
comment|/**    * Pulls data from ZooKeeper.  If isInit is false, it will only parse the    * next secret and version.  If isInit is true, it will also parse the current    * and previous secrets, and the next rollover date; it will also init the    * secrets.  Hence, isInit should only be true on startup.    * @param isInit  see description above    */
DECL|method|pullFromZK (boolean isInit)
specifier|private
specifier|synchronized
name|void
name|pullFromZK
parameter_list|(
name|boolean
name|isInit
parameter_list|)
block|{
try|try
block|{
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|client
operator|.
name|getData
argument_list|()
operator|.
name|storingStatIn
argument_list|(
name|stat
argument_list|)
operator|.
name|forPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|ByteBuffer
name|bb
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|int
name|dataVersion
init|=
name|bb
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|dataVersion
operator|>
name|DATA_VERSION
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot load data from ZooKeeper; it"
operator|+
literal|"was written with a newer version"
argument_list|)
throw|;
block|}
name|int
name|nextSecretLength
init|=
name|bb
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|nextSecret
init|=
operator|new
name|byte
index|[
name|nextSecretLength
index|]
decl_stmt|;
name|bb
operator|.
name|get
argument_list|(
name|nextSecret
argument_list|)
expr_stmt|;
name|this
operator|.
name|nextSecret
operator|=
name|nextSecret
expr_stmt|;
name|zkVersion
operator|=
name|stat
operator|.
name|getVersion
argument_list|()
expr_stmt|;
if|if
condition|(
name|isInit
condition|)
block|{
name|int
name|currentSecretLength
init|=
name|bb
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|currentSecret
init|=
operator|new
name|byte
index|[
name|currentSecretLength
index|]
decl_stmt|;
name|bb
operator|.
name|get
argument_list|(
name|currentSecret
argument_list|)
expr_stmt|;
name|int
name|previousSecretLength
init|=
name|bb
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|previousSecret
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|previousSecretLength
operator|>
literal|0
condition|)
block|{
name|previousSecret
operator|=
operator|new
name|byte
index|[
name|previousSecretLength
index|]
expr_stmt|;
name|bb
operator|.
name|get
argument_list|(
name|previousSecret
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|initSecrets
argument_list|(
name|currentSecret
argument_list|,
name|previousSecret
argument_list|)
expr_stmt|;
name|nextRolloverDate
operator|=
name|bb
operator|.
name|getLong
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"An unexpected exception occurred while pulling data from"
operator|+
literal|"ZooKeeper"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|generateRandomSecret ()
specifier|private
name|byte
index|[]
name|generateRandomSecret
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|.
name|getBytes
argument_list|()
return|;
block|}
comment|/**    * This method creates the Curator client and connects to ZooKeeper.    * @param config configuration properties    * @return A Curator client    * @throws java.lang.Exception    */
DECL|method|createCuratorClient (Properties config)
specifier|protected
name|CuratorFramework
name|createCuratorClient
parameter_list|(
name|Properties
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|connectionString
init|=
name|config
operator|.
name|getProperty
argument_list|(
name|ZOOKEEPER_CONNECTION_STRING
argument_list|,
literal|"localhost:2181"
argument_list|)
decl_stmt|;
name|RetryPolicy
name|retryPolicy
init|=
operator|new
name|ExponentialBackoffRetry
argument_list|(
literal|1000
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|ACLProvider
name|aclProvider
decl_stmt|;
name|String
name|authType
init|=
name|config
operator|.
name|getProperty
argument_list|(
name|ZOOKEEPER_AUTH_TYPE
argument_list|,
literal|"none"
argument_list|)
decl_stmt|;
if|if
condition|(
name|authType
operator|.
name|equals
argument_list|(
literal|"sasl"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting to ZooKeeper with SASL/Kerberos"
operator|+
literal|"and using 'sasl' ACLs"
argument_list|)
expr_stmt|;
name|String
name|principal
init|=
name|setJaasConfiguration
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ZooKeeperSaslClient
operator|.
name|LOGIN_CONTEXT_NAME_KEY
argument_list|,
literal|"ZKSignerSecretProviderClient"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zookeeper.authProvider.1"
argument_list|,
literal|"org.apache.zookeeper.server.auth.SASLAuthenticationProvider"
argument_list|)
expr_stmt|;
name|aclProvider
operator|=
operator|new
name|SASLOwnerACLProvider
argument_list|(
name|principal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// "none"
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting to ZooKeeper without authentication"
argument_list|)
expr_stmt|;
name|aclProvider
operator|=
operator|new
name|DefaultACLProvider
argument_list|()
expr_stmt|;
comment|// open to everyone
block|}
name|CuratorFramework
name|cf
init|=
name|CuratorFrameworkFactory
operator|.
name|builder
argument_list|()
operator|.
name|connectString
argument_list|(
name|connectionString
argument_list|)
operator|.
name|retryPolicy
argument_list|(
name|retryPolicy
argument_list|)
operator|.
name|aclProvider
argument_list|(
name|aclProvider
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cf
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|cf
return|;
block|}
DECL|method|setJaasConfiguration (Properties config)
specifier|private
name|String
name|setJaasConfiguration
parameter_list|(
name|Properties
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|keytabFile
init|=
name|config
operator|.
name|getProperty
argument_list|(
name|ZOOKEEPER_KERBEROS_KEYTAB
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|keytabFile
operator|==
literal|null
operator|||
name|keytabFile
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ZOOKEEPER_KERBEROS_KEYTAB
operator|+
literal|" must be specified"
argument_list|)
throw|;
block|}
name|String
name|principal
init|=
name|config
operator|.
name|getProperty
argument_list|(
name|ZOOKEEPER_KERBEROS_PRINCIPAL
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|principal
operator|==
literal|null
operator|||
name|principal
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ZOOKEEPER_KERBEROS_PRINCIPAL
operator|+
literal|" must be specified"
argument_list|)
throw|;
block|}
comment|// This is equivalent to writing a jaas.conf file and setting the system
comment|// property, "java.security.auth.login.config", to point to it
name|JaasConfiguration
name|jConf
init|=
operator|new
name|JaasConfiguration
argument_list|(
literal|"Client"
argument_list|,
name|principal
argument_list|,
name|keytabFile
argument_list|)
decl_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|jConf
argument_list|)
expr_stmt|;
return|return
name|principal
operator|.
name|split
argument_list|(
literal|"[/@]"
argument_list|)
index|[
literal|0
index|]
return|;
block|}
comment|/**    * Simple implementation of an {@link ACLProvider} that simply returns an ACL    * that gives all permissions only to a single principal.    */
DECL|class|SASLOwnerACLProvider
specifier|private
specifier|static
class|class
name|SASLOwnerACLProvider
implements|implements
name|ACLProvider
block|{
DECL|field|saslACL
specifier|private
specifier|final
name|List
argument_list|<
name|ACL
argument_list|>
name|saslACL
decl_stmt|;
DECL|method|SASLOwnerACLProvider (String principal)
specifier|private
name|SASLOwnerACLProvider
parameter_list|(
name|String
name|principal
parameter_list|)
block|{
name|this
operator|.
name|saslACL
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ACL
argument_list|(
name|Perms
operator|.
name|ALL
argument_list|,
operator|new
name|Id
argument_list|(
literal|"sasl"
argument_list|,
name|principal
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDefaultAcl ()
specifier|public
name|List
argument_list|<
name|ACL
argument_list|>
name|getDefaultAcl
parameter_list|()
block|{
return|return
name|saslACL
return|;
block|}
annotation|@
name|Override
DECL|method|getAclForPath (String path)
specifier|public
name|List
argument_list|<
name|ACL
argument_list|>
name|getAclForPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|saslACL
return|;
block|}
block|}
comment|/**    * Creates a programmatic version of a jaas.conf file. This can be used    * instead of writing a jaas.conf file and setting the system property,    * "java.security.auth.login.config", to point to that file. It is meant to be    * used for connecting to ZooKeeper.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|JaasConfiguration
specifier|public
specifier|static
class|class
name|JaasConfiguration
extends|extends
name|Configuration
block|{
DECL|field|entry
specifier|private
specifier|static
name|AppConfigurationEntry
index|[]
name|entry
decl_stmt|;
DECL|field|entryName
specifier|private
name|String
name|entryName
decl_stmt|;
comment|/**      * Add an entry to the jaas configuration with the passed in name,      * principal, and keytab. The other necessary options will be set for you.      *      * @param entryName The name of the entry (e.g. "Client")      * @param principal The principal of the user      * @param keytab The location of the keytab      */
DECL|method|JaasConfiguration (String entryName, String principal, String keytab)
specifier|public
name|JaasConfiguration
parameter_list|(
name|String
name|entryName
parameter_list|,
name|String
name|principal
parameter_list|,
name|String
name|keytab
parameter_list|)
block|{
name|this
operator|.
name|entryName
operator|=
name|entryName
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"keyTab"
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"principal"
argument_list|,
name|principal
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"useKeyTab"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"storeKey"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"useTicketCache"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"refreshKrb5Config"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|String
name|jaasEnvVar
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"HADOOP_JAAS_DEBUG"
argument_list|)
decl_stmt|;
if|if
condition|(
name|jaasEnvVar
operator|!=
literal|null
operator|&&
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|jaasEnvVar
argument_list|)
condition|)
block|{
name|options
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|entry
operator|=
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|getKrb5LoginModuleName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|options
argument_list|)
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAppConfigurationEntry (String name)
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|entryName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|)
condition|?
name|entry
else|:
literal|null
return|;
block|}
DECL|method|getKrb5LoginModuleName ()
specifier|private
name|String
name|getKrb5LoginModuleName
parameter_list|()
block|{
name|String
name|krb5LoginModuleName
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"IBM"
argument_list|)
condition|)
block|{
name|krb5LoginModuleName
operator|=
literal|"com.ibm.security.auth.module.Krb5LoginModule"
expr_stmt|;
block|}
else|else
block|{
name|krb5LoginModuleName
operator|=
literal|"com.sun.security.auth.module.Krb5LoginModule"
expr_stmt|;
block|}
return|return
name|krb5LoginModuleName
return|;
block|}
block|}
block|}
end_class

end_unit

