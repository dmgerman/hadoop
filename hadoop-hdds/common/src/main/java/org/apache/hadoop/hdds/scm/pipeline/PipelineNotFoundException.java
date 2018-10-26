begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipeline
package|package
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
name|pipeline
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

begin_comment
comment|/**  * Signals that a pipeline is missing from PipelineManager.  */
end_comment

begin_class
DECL|class|PipelineNotFoundException
specifier|public
class|class
name|PipelineNotFoundException
extends|extends
name|IOException
block|{
comment|/**    * Constructs an {@code PipelineNotFoundException} with {@code null}    * as its error detail message.    */
DECL|method|PipelineNotFoundException ()
specifier|public
name|PipelineNotFoundException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructs an {@code PipelineNotFoundException} with the specified    * detail message.    *    * @param message    *        The detail message (which is saved for later retrieval    *        by the {@link #getMessage()} method)    */
DECL|method|PipelineNotFoundException (String message)
specifier|public
name|PipelineNotFoundException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

