begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|ha
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
name|ha
operator|.
name|ServiceFailedException
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
operator|.
name|OperationCategory
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
name|ipc
operator|.
name|StandbyException
import|;
end_import

begin_comment
comment|/**  * Context that is to be used by {@link HAState} for getting/setting the  * current state and performing required operations.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|HAContext
specifier|public
interface|interface
name|HAContext
block|{
comment|/** Set the state of the context to given {@code state} */
DECL|method|setState (HAState state)
specifier|public
name|void
name|setState
parameter_list|(
name|HAState
name|state
parameter_list|)
function_decl|;
comment|/** Get the state from the context */
DECL|method|getState ()
specifier|public
name|HAState
name|getState
parameter_list|()
function_decl|;
comment|/** Start the services required in active state */
DECL|method|startActiveServices ()
specifier|public
name|void
name|startActiveServices
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Stop the services when exiting active state */
DECL|method|stopActiveServices ()
specifier|public
name|void
name|stopActiveServices
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Start the services required in standby state */
DECL|method|startStandbyServices ()
specifier|public
name|void
name|startStandbyServices
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Prepare to exit the standby state */
DECL|method|prepareToStopStandbyServices ()
specifier|public
name|void
name|prepareToStopStandbyServices
parameter_list|()
throws|throws
name|ServiceFailedException
function_decl|;
comment|/** Stop the services when exiting standby state */
DECL|method|stopStandbyServices ()
specifier|public
name|void
name|stopStandbyServices
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Take a write-lock on the underlying namesystem    * so that no concurrent state transitions or edits    * can be made.    */
DECL|method|writeLock ()
name|void
name|writeLock
parameter_list|()
function_decl|;
comment|/**    * Unlock the lock taken by {@link #writeLock()}    */
DECL|method|writeUnlock ()
name|void
name|writeUnlock
parameter_list|()
function_decl|;
comment|/**    * Verify that the given operation category is allowed in the    * current state. This is to allow NN implementations (eg BackupNode)    * to override it with node-specific handling.    */
DECL|method|checkOperation (OperationCategory op)
name|void
name|checkOperation
parameter_list|(
name|OperationCategory
name|op
parameter_list|)
throws|throws
name|StandbyException
function_decl|;
comment|/**    * @return true if the node should allow stale reads (ie reads    * while the namespace is not up to date)    */
DECL|method|allowStaleReads ()
name|boolean
name|allowStaleReads
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

