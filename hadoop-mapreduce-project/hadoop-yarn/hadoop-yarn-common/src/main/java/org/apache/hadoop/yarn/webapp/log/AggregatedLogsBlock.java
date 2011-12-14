begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.webapp.log
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|log
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
name|yarn
operator|.
name|webapp
operator|.
name|YarnWebParams
operator|.
name|APP_OWNER
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
name|yarn
operator|.
name|webapp
operator|.
name|YarnWebParams
operator|.
name|CONTAINER_ID
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
name|yarn
operator|.
name|webapp
operator|.
name|YarnWebParams
operator|.
name|ENTITY_STRING
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
name|yarn
operator|.
name|webapp
operator|.
name|YarnWebParams
operator|.
name|NM_NODENAME
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|Map
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
name|Path
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAccessType
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|logaggregation
operator|.
name|AggregatedLogFormat
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
name|yarn
operator|.
name|logaggregation
operator|.
name|AggregatedLogFormat
operator|.
name|LogKey
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
name|yarn
operator|.
name|logaggregation
operator|.
name|LogAggregationUtils
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
name|yarn
operator|.
name|server
operator|.
name|security
operator|.
name|ApplicationACLsManager
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
name|yarn
operator|.
name|util
operator|.
name|ConverterUtils
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
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|HtmlBlock
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
DECL|class|AggregatedLogsBlock
specifier|public
class|class
name|AggregatedLogsBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Inject
DECL|method|AggregatedLogsBlock (Configuration conf)
name|AggregatedLogsBlock
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|ContainerId
name|containerId
init|=
name|verifyAndGetContainerId
argument_list|(
name|html
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|verifyAndGetNodeId
argument_list|(
name|html
argument_list|)
decl_stmt|;
name|String
name|appOwner
init|=
name|verifyAndGetAppOwner
argument_list|(
name|html
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerId
operator|==
literal|null
operator|||
name|nodeId
operator|==
literal|null
operator|||
name|appOwner
operator|==
literal|null
operator|||
name|appOwner
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|ApplicationId
name|applicationId
init|=
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|String
name|logEntity
init|=
name|$
argument_list|(
name|ENTITY_STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|logEntity
operator|==
literal|null
operator|||
name|logEntity
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|logEntity
operator|=
name|containerId
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_LOG_AGGREGATION_ENABLED
argument_list|)
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Aggregation is not enabled. Try the nodemanager at "
operator|+
name|nodeId
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return;
block|}
name|Path
name|remoteRootLogDir
init|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_REMOTE_APP_LOG_DIR
argument_list|)
argument_list|)
decl_stmt|;
name|AggregatedLogFormat
operator|.
name|LogReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|AggregatedLogFormat
operator|.
name|LogReader
argument_list|(
name|conf
argument_list|,
name|LogAggregationUtils
operator|.
name|getRemoteNodeLogFileForApp
argument_list|(
name|remoteRootLogDir
argument_list|,
name|applicationId
argument_list|,
name|appOwner
argument_list|,
name|nodeId
argument_list|,
name|LogAggregationUtils
operator|.
name|getRemoteNodeLogDirSuffix
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// ACLs not available till the log file is opened.
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Logs not available for "
operator|+
name|logEntity
operator|+
literal|". Aggregation may not be complete, "
operator|+
literal|"Check back later or try the nodemanager at "
operator|+
name|nodeId
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Error getting logs for "
operator|+
name|logEntity
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Error getting logs for "
operator|+
name|logEntity
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|owner
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|appAcls
init|=
literal|null
decl_stmt|;
try|try
block|{
name|owner
operator|=
name|reader
operator|.
name|getApplicationOwner
argument_list|()
expr_stmt|;
name|appAcls
operator|=
name|reader
operator|.
name|getApplicationAcls
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Error getting logs for "
operator|+
name|logEntity
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Error getting logs for "
operator|+
name|logEntity
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|ApplicationACLsManager
name|aclsManager
init|=
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|aclsManager
operator|.
name|addApplication
argument_list|(
name|applicationId
argument_list|,
name|appAcls
argument_list|)
expr_stmt|;
name|String
name|remoteUser
init|=
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|callerUGI
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|remoteUser
operator|!=
literal|null
condition|)
block|{
name|callerUGI
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|remoteUser
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|callerUGI
operator|!=
literal|null
operator|&&
operator|!
name|aclsManager
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|owner
argument_list|,
name|applicationId
argument_list|)
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"User ["
operator|+
name|remoteUser
operator|+
literal|"] is not authorized to view the logs for "
operator|+
name|logEntity
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return;
block|}
name|DataInputStream
name|valueStream
decl_stmt|;
name|LogKey
name|key
init|=
operator|new
name|LogKey
argument_list|()
decl_stmt|;
try|try
block|{
name|valueStream
operator|=
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|)
expr_stmt|;
while|while
condition|(
name|valueStream
operator|!=
literal|null
operator|&&
operator|!
name|key
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|valueStream
operator|=
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|valueStream
operator|==
literal|null
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Logs not available for "
operator|+
name|logEntity
operator|+
literal|". Could be caused by the rentention policy"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return;
block|}
name|writer
argument_list|()
operator|.
name|write
argument_list|(
literal|"<pre>"
argument_list|)
expr_stmt|;
name|AggregatedLogFormat
operator|.
name|LogReader
operator|.
name|readAcontainerLogs
argument_list|(
name|valueStream
argument_list|,
name|writer
argument_list|()
argument_list|)
expr_stmt|;
name|writer
argument_list|()
operator|.
name|write
argument_list|(
literal|"</pre>"
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Error getting logs for "
operator|+
name|logEntity
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Error getting logs for "
operator|+
name|logEntity
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
DECL|method|verifyAndGetContainerId (Block html)
specifier|private
name|ContainerId
name|verifyAndGetContainerId
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|String
name|containerIdStr
init|=
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerIdStr
operator|==
literal|null
operator|||
name|containerIdStr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Cannot get container logs without a ContainerId"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
name|ContainerId
name|containerId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|containerId
operator|=
name|ConverterUtils
operator|.
name|toContainerId
argument_list|(
name|containerIdStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Cannot get container logs for invalid containerId: "
operator|+
name|containerIdStr
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|containerId
return|;
block|}
DECL|method|verifyAndGetNodeId (Block html)
specifier|private
name|NodeId
name|verifyAndGetNodeId
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|String
name|nodeIdStr
init|=
name|$
argument_list|(
name|NM_NODENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeIdStr
operator|==
literal|null
operator|||
name|nodeIdStr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Cannot get container logs without a NodeId"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
name|NodeId
name|nodeId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|nodeId
operator|=
name|ConverterUtils
operator|.
name|toNodeId
argument_list|(
name|nodeIdStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Cannot get container logs. Invalid nodeId: "
operator|+
name|nodeIdStr
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|nodeId
return|;
block|}
DECL|method|verifyAndGetAppOwner (Block html)
specifier|private
name|String
name|verifyAndGetAppOwner
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|String
name|appOwner
init|=
name|$
argument_list|(
name|APP_OWNER
argument_list|)
decl_stmt|;
if|if
condition|(
name|appOwner
operator|==
literal|null
operator|||
name|appOwner
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|_
argument_list|(
literal|"Cannot get container logs without an app owner"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
return|return
name|appOwner
return|;
block|}
block|}
end_class

end_unit

