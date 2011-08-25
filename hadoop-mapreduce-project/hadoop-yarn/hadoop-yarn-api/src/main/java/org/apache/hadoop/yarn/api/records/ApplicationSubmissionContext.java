begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
package|;
end_package

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

begin_interface
DECL|interface|ApplicationSubmissionContext
specifier|public
interface|interface
name|ApplicationSubmissionContext
block|{
DECL|method|getApplicationId ()
specifier|public
specifier|abstract
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
DECL|method|getApplicationName ()
specifier|public
specifier|abstract
name|String
name|getApplicationName
parameter_list|()
function_decl|;
DECL|method|getMasterCapability ()
specifier|public
specifier|abstract
name|Resource
name|getMasterCapability
parameter_list|()
function_decl|;
DECL|method|getAllResources ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|URL
argument_list|>
name|getAllResources
parameter_list|()
function_decl|;
DECL|method|getResource (String key)
specifier|public
specifier|abstract
name|URL
name|getResource
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|getAllResourcesTodo ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|getAllResourcesTodo
parameter_list|()
function_decl|;
DECL|method|getResourceTodo (String key)
specifier|public
specifier|abstract
name|LocalResource
name|getResourceTodo
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|getFsTokenList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getFsTokenList
parameter_list|()
function_decl|;
DECL|method|getFsToken (int index)
specifier|public
specifier|abstract
name|String
name|getFsToken
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getFsTokenCount ()
specifier|public
specifier|abstract
name|int
name|getFsTokenCount
parameter_list|()
function_decl|;
DECL|method|getFsTokensTodo ()
specifier|public
specifier|abstract
name|ByteBuffer
name|getFsTokensTodo
parameter_list|()
function_decl|;
DECL|method|getAllEnvironment ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAllEnvironment
parameter_list|()
function_decl|;
DECL|method|getEnvironment (String key)
specifier|public
specifier|abstract
name|String
name|getEnvironment
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|getCommandList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getCommandList
parameter_list|()
function_decl|;
DECL|method|getCommand (int index)
specifier|public
specifier|abstract
name|String
name|getCommand
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getCommandCount ()
specifier|public
specifier|abstract
name|int
name|getCommandCount
parameter_list|()
function_decl|;
DECL|method|getQueue ()
specifier|public
specifier|abstract
name|String
name|getQueue
parameter_list|()
function_decl|;
DECL|method|getPriority ()
specifier|public
specifier|abstract
name|Priority
name|getPriority
parameter_list|()
function_decl|;
DECL|method|getUser ()
specifier|public
specifier|abstract
name|String
name|getUser
parameter_list|()
function_decl|;
DECL|method|setApplicationId (ApplicationId appplicationId)
specifier|public
specifier|abstract
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|appplicationId
parameter_list|)
function_decl|;
DECL|method|setApplicationName (String applicationName)
specifier|public
specifier|abstract
name|void
name|setApplicationName
parameter_list|(
name|String
name|applicationName
parameter_list|)
function_decl|;
DECL|method|setMasterCapability (Resource masterCapability)
specifier|public
specifier|abstract
name|void
name|setMasterCapability
parameter_list|(
name|Resource
name|masterCapability
parameter_list|)
function_decl|;
DECL|method|addAllResources (Map<String, URL> resources)
specifier|public
specifier|abstract
name|void
name|addAllResources
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|URL
argument_list|>
name|resources
parameter_list|)
function_decl|;
DECL|method|setResource (String key, URL url)
specifier|public
specifier|abstract
name|void
name|setResource
parameter_list|(
name|String
name|key
parameter_list|,
name|URL
name|url
parameter_list|)
function_decl|;
DECL|method|removeResource (String key)
specifier|public
specifier|abstract
name|void
name|removeResource
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|clearResources ()
specifier|public
specifier|abstract
name|void
name|clearResources
parameter_list|()
function_decl|;
DECL|method|addAllResourcesTodo (Map<String, LocalResource> resourcesTodo)
specifier|public
specifier|abstract
name|void
name|addAllResourcesTodo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|resourcesTodo
parameter_list|)
function_decl|;
DECL|method|setResourceTodo (String key, LocalResource localResource)
specifier|public
specifier|abstract
name|void
name|setResourceTodo
parameter_list|(
name|String
name|key
parameter_list|,
name|LocalResource
name|localResource
parameter_list|)
function_decl|;
DECL|method|removeResourceTodo (String key)
specifier|public
specifier|abstract
name|void
name|removeResourceTodo
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|clearResourcesTodo ()
specifier|public
specifier|abstract
name|void
name|clearResourcesTodo
parameter_list|()
function_decl|;
DECL|method|addAllFsTokens (List<String> fsTokens)
specifier|public
specifier|abstract
name|void
name|addAllFsTokens
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fsTokens
parameter_list|)
function_decl|;
DECL|method|addFsToken (String fsToken)
specifier|public
specifier|abstract
name|void
name|addFsToken
parameter_list|(
name|String
name|fsToken
parameter_list|)
function_decl|;
DECL|method|removeFsToken (int index)
specifier|public
specifier|abstract
name|void
name|removeFsToken
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearFsTokens ()
specifier|public
specifier|abstract
name|void
name|clearFsTokens
parameter_list|()
function_decl|;
DECL|method|setFsTokensTodo (ByteBuffer fsTokensTodo)
specifier|public
specifier|abstract
name|void
name|setFsTokensTodo
parameter_list|(
name|ByteBuffer
name|fsTokensTodo
parameter_list|)
function_decl|;
DECL|method|addAllEnvironment (Map<String, String> environment)
specifier|public
specifier|abstract
name|void
name|addAllEnvironment
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|)
function_decl|;
DECL|method|setEnvironment (String key, String env)
specifier|public
specifier|abstract
name|void
name|setEnvironment
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|env
parameter_list|)
function_decl|;
DECL|method|removeEnvironment (String key)
specifier|public
specifier|abstract
name|void
name|removeEnvironment
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|clearEnvironment ()
specifier|public
specifier|abstract
name|void
name|clearEnvironment
parameter_list|()
function_decl|;
DECL|method|addAllCommands (List<String> commands)
specifier|public
specifier|abstract
name|void
name|addAllCommands
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|commands
parameter_list|)
function_decl|;
DECL|method|addCommand (String command)
specifier|public
specifier|abstract
name|void
name|addCommand
parameter_list|(
name|String
name|command
parameter_list|)
function_decl|;
DECL|method|removeCommand (int index)
specifier|public
specifier|abstract
name|void
name|removeCommand
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearCommands ()
specifier|public
specifier|abstract
name|void
name|clearCommands
parameter_list|()
function_decl|;
DECL|method|setQueue (String queue)
specifier|public
specifier|abstract
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
function_decl|;
DECL|method|setPriority (Priority priority)
specifier|public
specifier|abstract
name|void
name|setPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
function_decl|;
DECL|method|setUser (String user)
specifier|public
specifier|abstract
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

