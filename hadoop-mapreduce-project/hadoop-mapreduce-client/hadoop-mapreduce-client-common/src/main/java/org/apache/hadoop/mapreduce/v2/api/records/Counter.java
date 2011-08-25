begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
package|;
end_package

begin_interface
DECL|interface|Counter
specifier|public
interface|interface
name|Counter
block|{
DECL|method|getName ()
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getDisplayName ()
specifier|public
specifier|abstract
name|String
name|getDisplayName
parameter_list|()
function_decl|;
DECL|method|getValue ()
specifier|public
specifier|abstract
name|long
name|getValue
parameter_list|()
function_decl|;
DECL|method|setName (String name)
specifier|public
specifier|abstract
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|setDisplayName (String displayName)
specifier|public
specifier|abstract
name|void
name|setDisplayName
parameter_list|(
name|String
name|displayName
parameter_list|)
function_decl|;
DECL|method|setValue (long value)
specifier|public
specifier|abstract
name|void
name|setValue
parameter_list|(
name|long
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

