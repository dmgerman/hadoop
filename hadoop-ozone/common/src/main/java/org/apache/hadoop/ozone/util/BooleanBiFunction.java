begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.ozone.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Defines a functional interface having two inputs and returns boolean as  * output.  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
DECL|interface|BooleanBiFunction
specifier|public
interface|interface
name|BooleanBiFunction
parameter_list|<
name|LEFT
parameter_list|,
name|RIGHT
parameter_list|>
block|{
DECL|method|apply (LEFT left, RIGHT right)
name|boolean
name|apply
parameter_list|(
name|LEFT
name|left
parameter_list|,
name|RIGHT
name|right
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

