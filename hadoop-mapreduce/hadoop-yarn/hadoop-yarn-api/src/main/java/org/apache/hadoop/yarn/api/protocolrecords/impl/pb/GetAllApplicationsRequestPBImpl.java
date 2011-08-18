begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords.impl.pb
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetAllApplicationsRequest
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
name|ProtoBase
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
name|proto
operator|.
name|YarnServiceProtos
operator|.
name|GetAllApplicationsRequestProto
import|;
end_import

begin_class
DECL|class|GetAllApplicationsRequestPBImpl
specifier|public
class|class
name|GetAllApplicationsRequestPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|GetAllApplicationsRequestProto
argument_list|>
implements|implements
name|GetAllApplicationsRequest
block|{
DECL|field|proto
name|GetAllApplicationsRequestProto
name|proto
init|=
name|GetAllApplicationsRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetAllApplicationsRequestProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|method|GetAllApplicationsRequestPBImpl ()
specifier|public
name|GetAllApplicationsRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetAllApplicationsRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetAllApplicationsRequestPBImpl (GetAllApplicationsRequestProto proto)
specifier|public
name|GetAllApplicationsRequestPBImpl
parameter_list|(
name|GetAllApplicationsRequestProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProto ()
specifier|public
name|GetAllApplicationsRequestProto
name|getProto
parameter_list|()
block|{
name|proto
operator|=
name|viaProto
condition|?
name|proto
else|:
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
return|return
name|proto
return|;
block|}
block|}
end_class

end_unit

