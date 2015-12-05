begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|List
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
name|ReconfigurationTaskStatus
import|;
end_import

begin_comment
comment|/**********************************************************************  * ReconfigurationProtocol is used by HDFS admin to reload configuration  * for NN/DN without restarting them.  **********************************************************************/
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|ReconfigurationProtocol
specifier|public
interface|interface
name|ReconfigurationProtocol
block|{
DECL|field|versionID
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**    * Asynchronously reload configuration on disk and apply changes.    */
DECL|method|startReconfiguration ()
name|void
name|startReconfiguration
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the status of the previously issued reconfig task.    * @see {@link org.apache.hadoop.conf.ReconfigurationTaskStatus}.    */
DECL|method|getReconfigurationStatus ()
name|ReconfigurationTaskStatus
name|getReconfigurationStatus
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a list of allowed properties for reconfiguration.    */
DECL|method|listReconfigurableProperties ()
name|List
argument_list|<
name|String
argument_list|>
name|listReconfigurableProperties
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

