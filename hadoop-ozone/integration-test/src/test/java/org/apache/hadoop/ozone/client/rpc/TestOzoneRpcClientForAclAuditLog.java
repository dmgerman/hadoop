begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.ozone.client.rpc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
operator|.
name|rpc
package|;
end_package

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|NotThreadSafe
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
name|io
operator|.
name|FileUtils
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
name|hadoop
operator|.
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|MiniOzoneCluster
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
name|OzoneAcl
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
name|audit
operator|.
name|AuditEventStatus
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
name|audit
operator|.
name|OMAction
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
name|client
operator|.
name|ObjectStore
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
name|client
operator|.
name|OzoneClient
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
name|client
operator|.
name|OzoneClientFactory
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
name|client
operator|.
name|OzoneVolume
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
name|client
operator|.
name|VolumeArgs
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
name|security
operator|.
name|acl
operator|.
name|IAccessAuthorizer
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
name|security
operator|.
name|acl
operator|.
name|OzoneObj
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
name|security
operator|.
name|acl
operator|.
name|OzoneObjInfo
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
name|security
operator|.
name|UserGroupInformation
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|FixMethodOrder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|MethodSorters
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
name|Arrays
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
name|UUID
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
name|OzoneAcl
operator|.
name|AclScope
operator|.
name|ACCESS
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
name|OzoneConfigKeys
operator|.
name|OZONE_ACL_AUTHORIZER_CLASS
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
name|OzoneConfigKeys
operator|.
name|OZONE_ACL_AUTHORIZER_CLASS_NATIVE
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
name|OzoneConfigKeys
operator|.
name|OZONE_ACL_ENABLED
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
name|OzoneConfigKeys
operator|.
name|OZONE_ADMINISTRATORS
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
name|OzoneConfigKeys
operator|.
name|OZONE_ADMINISTRATORS_WILDCARD
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
name|security
operator|.
name|acl
operator|.
name|OzoneObj
operator|.
name|ResourceType
operator|.
name|VOLUME
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
name|security
operator|.
name|acl
operator|.
name|OzoneObj
operator|.
name|StoreType
operator|.
name|OZONE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * This class is to test audit logs for xxxACL APIs of Ozone Client.  * It is annotated as NotThreadSafe intentionally since this test reads from  * the generated audit logs to verify the operations. Since the  * maven test plugin will trigger parallel test execution, there is a  * possibility of other audit events being logged and leading to failure of  * all assertion based test in this class.  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
annotation|@
name|FixMethodOrder
argument_list|(
name|MethodSorters
operator|.
name|NAME_ASCENDING
argument_list|)
DECL|class|TestOzoneRpcClientForAclAuditLog
specifier|public
class|class
name|TestOzoneRpcClientForAclAuditLog
block|{
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
name|TestOzoneRpcClientForAclAuditLog
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ugi
specifier|private
specifier|static
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|USER_ACL
specifier|private
specifier|static
specifier|final
name|OzoneAcl
name|USER_ACL
init|=
operator|new
name|OzoneAcl
argument_list|(
name|IAccessAuthorizer
operator|.
name|ACLIdentityType
operator|.
name|USER
argument_list|,
literal|"johndoe"
argument_list|,
name|IAccessAuthorizer
operator|.
name|ACLType
operator|.
name|ALL
argument_list|,
name|ACCESS
argument_list|)
decl_stmt|;
DECL|field|USER_ACL_2
specifier|private
specifier|static
specifier|final
name|OzoneAcl
name|USER_ACL_2
init|=
operator|new
name|OzoneAcl
argument_list|(
name|IAccessAuthorizer
operator|.
name|ACLIdentityType
operator|.
name|USER
argument_list|,
literal|"jane"
argument_list|,
name|IAccessAuthorizer
operator|.
name|ACLType
operator|.
name|ALL
argument_list|,
name|ACCESS
argument_list|)
decl_stmt|;
DECL|field|aclListToAdd
specifier|private
specifier|static
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|aclListToAdd
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|ozClient
specifier|private
specifier|static
name|OzoneClient
name|ozClient
init|=
literal|null
decl_stmt|;
DECL|field|store
specifier|private
specifier|static
name|ObjectStore
name|store
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
DECL|field|scmId
specifier|private
specifier|static
name|String
name|scmId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|/**    * Create a MiniOzoneCluster for testing.    *    * Ozone is made active by setting OZONE_ENABLED = true    *    * @throws IOException    */
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"log4j.configurationFile"
argument_list|,
literal|"log4j2.properties"
argument_list|)
expr_stmt|;
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OZONE_ACL_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_ADMINISTRATORS
argument_list|,
name|OZONE_ADMINISTRATORS_WILDCARD
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_ACL_AUTHORIZER_CLASS
argument_list|,
name|OZONE_ACL_AUTHORIZER_CLASS_NATIVE
argument_list|)
expr_stmt|;
name|startCluster
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|aclListToAdd
operator|.
name|add
argument_list|(
name|USER_ACL
argument_list|)
expr_stmt|;
name|aclListToAdd
operator|.
name|add
argument_list|(
name|USER_ACL_2
argument_list|)
expr_stmt|;
name|emptyAuditLog
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a MiniOzoneCluster for testing.    * @param conf Configurations to start the cluster.    * @throws Exception    */
DECL|method|startCluster (OzoneConfiguration conf)
specifier|private
specifier|static
name|void
name|startCluster
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|3
argument_list|)
operator|.
name|setScmId
argument_list|(
name|scmId
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|ozClient
operator|=
name|OzoneClientFactory
operator|.
name|getRpcClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|store
operator|=
name|ozClient
operator|.
name|getObjectStore
argument_list|()
expr_stmt|;
name|storageContainerLocationClient
operator|=
name|cluster
operator|.
name|getStorageContainerLocationClient
argument_list|()
expr_stmt|;
block|}
comment|/**    * Close OzoneClient and shutdown MiniOzoneCluster.    */
annotation|@
name|AfterClass
DECL|method|teardown ()
specifier|public
specifier|static
name|void
name|teardown
parameter_list|()
throws|throws
name|IOException
block|{
name|shutdownCluster
argument_list|()
expr_stmt|;
name|deleteAuditLog
argument_list|()
expr_stmt|;
block|}
DECL|method|deleteAuditLog ()
specifier|private
specifier|static
name|void
name|deleteAuditLog
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"audit.log"
argument_list|)
decl_stmt|;
if|if
condition|(
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|" has been deleted."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"audit.log could not be deleted."
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|emptyAuditLog ()
specifier|private
specifier|static
name|void
name|emptyAuditLog
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"audit.log"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|writeLines
argument_list|(
name|file
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Close OzoneClient and shutdown MiniOzoneCluster.    */
DECL|method|shutdownCluster ()
specifier|private
specifier|static
name|void
name|shutdownCluster
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|ozClient
operator|!=
literal|null
condition|)
block|{
name|ozClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|storageContainerLocationClient
operator|!=
literal|null
condition|)
block|{
name|storageContainerLocationClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testXXXAclSuccessAudits ()
specifier|public
name|void
name|testXXXAclSuccessAudits
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|userName
init|=
name|ugi
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|String
name|adminName
init|=
name|ugi
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|VolumeArgs
name|createVolumeArgs
init|=
name|VolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAdmin
argument_list|(
name|adminName
argument_list|)
operator|.
name|setOwner
argument_list|(
name|userName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|store
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|CREATE_VOLUME
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
name|AuditEventStatus
operator|.
name|SUCCESS
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|OzoneVolume
name|retVolumeinfo
init|=
name|store
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|READ_VOLUME
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
name|AuditEventStatus
operator|.
name|SUCCESS
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|retVolumeinfo
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|volumeName
argument_list|)
argument_list|)
expr_stmt|;
name|OzoneObj
name|volObj
init|=
operator|new
name|OzoneObjInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setResType
argument_list|(
name|VOLUME
argument_list|)
operator|.
name|setStoreType
argument_list|(
name|OZONE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|//Testing getAcl
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
init|=
name|store
operator|.
name|getAcl
argument_list|(
name|volObj
argument_list|)
decl_stmt|;
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|GET_ACL
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
name|AuditEventStatus
operator|.
name|SUCCESS
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|acls
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|//Testing addAcl
name|store
operator|.
name|addAcl
argument_list|(
name|volObj
argument_list|,
name|USER_ACL
argument_list|)
expr_stmt|;
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|ADD_ACL
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
literal|"johndoe"
argument_list|,
name|AuditEventStatus
operator|.
name|SUCCESS
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
comment|//Testing removeAcl
name|store
operator|.
name|removeAcl
argument_list|(
name|volObj
argument_list|,
name|USER_ACL
argument_list|)
expr_stmt|;
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|REMOVE_ACL
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
literal|"johndoe"
argument_list|,
name|AuditEventStatus
operator|.
name|SUCCESS
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
comment|//Testing setAcl
name|store
operator|.
name|setAcl
argument_list|(
name|volObj
argument_list|,
name|aclListToAdd
argument_list|)
expr_stmt|;
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|SET_ACL
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
literal|"johndoe"
argument_list|,
literal|"jane"
argument_list|,
name|AuditEventStatus
operator|.
name|SUCCESS
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testXXXAclFailureAudits ()
specifier|public
name|void
name|testXXXAclFailureAudits
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|userName
init|=
literal|"bilbo"
decl_stmt|;
name|String
name|adminName
init|=
literal|"bilbo"
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|VolumeArgs
name|createVolumeArgs
init|=
name|VolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAdmin
argument_list|(
name|adminName
argument_list|)
operator|.
name|setOwner
argument_list|(
name|userName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|store
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|CREATE_VOLUME
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
name|AuditEventStatus
operator|.
name|SUCCESS
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|OzoneObj
name|volObj
init|=
operator|new
name|OzoneObjInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setResType
argument_list|(
name|VOLUME
argument_list|)
operator|.
name|setStoreType
argument_list|(
name|OZONE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// xxxAcl will fail as current ugi user doesn't have the required access
comment|// for volume
try|try
block|{
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
init|=
name|store
operator|.
name|getAcl
argument_list|(
name|volObj
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|GET_ACL
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
name|AuditEventStatus
operator|.
name|FAILURE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|store
operator|.
name|addAcl
argument_list|(
name|volObj
argument_list|,
name|USER_ACL
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|ADD_ACL
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
name|AuditEventStatus
operator|.
name|FAILURE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|store
operator|.
name|removeAcl
argument_list|(
name|volObj
argument_list|,
name|USER_ACL
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|REMOVE_ACL
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
name|AuditEventStatus
operator|.
name|FAILURE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|store
operator|.
name|setAcl
argument_list|(
name|volObj
argument_list|,
name|aclListToAdd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|verifyLog
argument_list|(
name|OMAction
operator|.
name|SET_ACL
operator|.
name|name
argument_list|()
argument_list|,
name|volumeName
argument_list|,
literal|"johndoe"
argument_list|,
literal|"jane"
argument_list|,
name|AuditEventStatus
operator|.
name|FAILURE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyLog (String... expected)
specifier|private
name|void
name|verifyLog
parameter_list|(
name|String
modifier|...
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"audit.log"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|FileUtils
operator|.
name|readLines
argument_list|(
name|file
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|(
operator|)
operator|->
operator|(
name|lines
operator|!=
literal|null
operator|)
condition|?
literal|true
else|:
literal|false
argument_list|,
literal|100
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
try|try
block|{
comment|// When log entry is expected, the log file will contain one line and
comment|// that must be equal to the expected string
name|assertTrue
argument_list|(
name|lines
operator|.
name|size
argument_list|()
operator|!=
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|exp
range|:
name|expected
control|)
block|{
name|assertTrue
argument_list|(
name|lines
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|contains
argument_list|(
name|exp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AssertionError
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error occurred in log verification"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
if|if
condition|(
name|lines
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Actual line ::: "
operator|+
name|lines
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Expected tokens ::: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|emptyAuditLog
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

