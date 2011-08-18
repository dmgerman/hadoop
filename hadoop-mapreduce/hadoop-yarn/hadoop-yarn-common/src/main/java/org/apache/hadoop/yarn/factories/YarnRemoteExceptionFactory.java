begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.factories
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|factories
package|;
end_package

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
name|exceptions
operator|.
name|YarnRemoteException
import|;
end_import

begin_interface
DECL|interface|YarnRemoteExceptionFactory
specifier|public
interface|interface
name|YarnRemoteExceptionFactory
block|{
DECL|method|createYarnRemoteException (String message)
specifier|public
name|YarnRemoteException
name|createYarnRemoteException
parameter_list|(
name|String
name|message
parameter_list|)
function_decl|;
DECL|method|createYarnRemoteException (String message, Throwable t)
specifier|public
name|YarnRemoteException
name|createYarnRemoteException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|t
parameter_list|)
function_decl|;
DECL|method|createYarnRemoteException (Throwable t)
specifier|public
name|YarnRemoteException
name|createYarnRemoteException
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

