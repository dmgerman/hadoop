begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
package|package
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
name|resourcemanager
package|;
end_package

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
name|Set
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|util
operator|.
name|HostsFileReader
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
name|YarnException
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_class
DECL|class|NodesListManager
specifier|public
class|class
name|NodesListManager
extends|extends
name|AbstractService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NodesListManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|hostsReader
specifier|private
name|HostsFileReader
name|hostsReader
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|NodesListManager ()
specifier|public
name|NodesListManager
parameter_list|()
block|{
name|super
argument_list|(
name|NodesListManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
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
comment|// Read the hosts/exclude files to restrict access to the RM
try|try
block|{
name|this
operator|.
name|hostsReader
operator|=
operator|new
name|HostsFileReader
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|RMConfig
operator|.
name|RM_NODES_INCLUDE_FILE
argument_list|,
name|RMConfig
operator|.
name|DEFAULT_RM_NODES_INCLUDE_FILE
argument_list|)
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|RMConfig
operator|.
name|RM_NODES_EXCLUDE_FILE
argument_list|,
name|RMConfig
operator|.
name|DEFAULT_RM_NODES_EXCLUDE_FILE
argument_list|)
argument_list|)
expr_stmt|;
name|printConfiguredHosts
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to init hostsReader, disabling"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|hostsReader
operator|=
operator|new
name|HostsFileReader
argument_list|(
name|RMConfig
operator|.
name|DEFAULT_RM_NODES_INCLUDE_FILE
argument_list|,
name|RMConfig
operator|.
name|DEFAULT_RM_NODES_EXCLUDE_FILE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe2
parameter_list|)
block|{
comment|// Should *never* happen
name|this
operator|.
name|hostsReader
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|ioe2
argument_list|)
throw|;
block|}
block|}
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|printConfiguredHosts ()
specifier|private
name|void
name|printConfiguredHosts
parameter_list|()
block|{
if|if
condition|(
operator|!
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"hostsReader: in="
operator|+
name|conf
operator|.
name|get
argument_list|(
name|RMConfig
operator|.
name|RM_NODES_INCLUDE_FILE
argument_list|,
name|RMConfig
operator|.
name|DEFAULT_RM_NODES_INCLUDE_FILE
argument_list|)
operator|+
literal|" out="
operator|+
name|conf
operator|.
name|get
argument_list|(
name|RMConfig
operator|.
name|RM_NODES_EXCLUDE_FILE
argument_list|,
name|RMConfig
operator|.
name|DEFAULT_RM_NODES_EXCLUDE_FILE
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|include
range|:
name|hostsReader
operator|.
name|getHosts
argument_list|()
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"include: "
operator|+
name|include
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|exclude
range|:
name|hostsReader
operator|.
name|getExcludedHosts
argument_list|()
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"exclude: "
operator|+
name|exclude
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|refreshNodes ()
specifier|public
name|void
name|refreshNodes
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|hostsReader
init|)
block|{
name|hostsReader
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|printConfiguredHosts
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|isValidNode (String hostName)
specifier|public
name|boolean
name|isValidNode
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
synchronized|synchronized
init|(
name|hostsReader
init|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|hostsList
init|=
name|hostsReader
operator|.
name|getHosts
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|excludeList
init|=
name|hostsReader
operator|.
name|getExcludedHosts
argument_list|()
decl_stmt|;
return|return
operator|(
operator|(
name|hostsList
operator|.
name|isEmpty
argument_list|()
operator|||
name|hostsList
operator|.
name|contains
argument_list|(
name|hostName
argument_list|)
operator|)
operator|&&
operator|!
name|excludeList
operator|.
name|contains
argument_list|(
name|hostName
argument_list|)
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

