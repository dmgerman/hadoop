begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.protocolrecords
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
name|protocolrecords
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_interface
DECL|interface|GetDiagnosticsResponse
specifier|public
interface|interface
name|GetDiagnosticsResponse
block|{
DECL|method|getDiagnosticsList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getDiagnosticsList
parameter_list|()
function_decl|;
DECL|method|getDiagnostics (int index)
specifier|public
specifier|abstract
name|String
name|getDiagnostics
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getDiagnosticsCount ()
specifier|public
specifier|abstract
name|int
name|getDiagnosticsCount
parameter_list|()
function_decl|;
DECL|method|addAllDiagnostics (List<String> diagnostics)
specifier|public
specifier|abstract
name|void
name|addAllDiagnostics
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|diagnostics
parameter_list|)
function_decl|;
DECL|method|addDiagnostics (String diagnostic)
specifier|public
specifier|abstract
name|void
name|addDiagnostics
parameter_list|(
name|String
name|diagnostic
parameter_list|)
function_decl|;
DECL|method|removeDiagnostics (int index)
specifier|public
specifier|abstract
name|void
name|removeDiagnostics
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearDiagnostics ()
specifier|public
specifier|abstract
name|void
name|clearDiagnostics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

