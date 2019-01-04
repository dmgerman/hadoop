begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.csi.translator
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|csi
operator|.
name|translator
package|;
end_package

begin_import
import|import
name|csi
operator|.
name|v0
operator|.
name|Csi
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
name|protocolrecords
operator|.
name|NodePublishVolumeRequest
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
name|protocolrecords
operator|.
name|NodeUnpublishVolumeRequest
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
name|protocolrecords
operator|.
name|ValidateVolumeCapabilitiesRequest
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
name|protocolrecords
operator|.
name|ValidateVolumeCapabilitiesResponse
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
name|protocolrecords
operator|.
name|GetPluginInfoResponse
import|;
end_import

begin_comment
comment|/**  * Factory class to get desired proto transformer instance.  */
end_comment

begin_class
DECL|class|ProtoTranslatorFactory
specifier|public
specifier|final
class|class
name|ProtoTranslatorFactory
block|{
DECL|method|ProtoTranslatorFactory ()
specifier|private
name|ProtoTranslatorFactory
parameter_list|()
block|{
comment|// hide constructor for the factory class
block|}
comment|/**    * Get a {@link ProtoTranslator} based on the given input message    * types. If the type is not supported, a IllegalArgumentException    * will be thrown. When adding more transformers to this factory class,    * note each transformer works exactly for one message to another    * (and vice versa). For each type of the message, make sure there is    * a corresponding unit test added, such as    * TestValidateVolumeCapabilitiesRequest.    *    * @param yarnProto yarn proto message    * @param csiProto CSI proto message    * @param<A> yarn proto message    * @param<B> CSI proto message    * @throws IllegalArgumentException    *   when given types are not supported    * @return    *   a proto message transformer that transforms    *   YARN internal proto message to CSI    */
DECL|method|getTranslator ( Class<A> yarnProto, Class<B> csiProto)
specifier|public
specifier|static
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
name|ProtoTranslator
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|getTranslator
parameter_list|(
name|Class
argument_list|<
name|A
argument_list|>
name|yarnProto
parameter_list|,
name|Class
argument_list|<
name|B
argument_list|>
name|csiProto
parameter_list|)
block|{
if|if
condition|(
name|yarnProto
operator|==
name|ValidateVolumeCapabilitiesRequest
operator|.
name|class
operator|&&
name|csiProto
operator|==
name|Csi
operator|.
name|ValidateVolumeCapabilitiesRequest
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|ValidateVolumeCapabilitiesRequestProtoTranslator
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|yarnProto
operator|==
name|ValidateVolumeCapabilitiesResponse
operator|.
name|class
operator|&&
name|csiProto
operator|==
name|Csi
operator|.
name|ValidateVolumeCapabilitiesResponse
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|ValidationVolumeCapabilitiesResponseProtoTranslator
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|yarnProto
operator|==
name|NodePublishVolumeRequest
operator|.
name|class
operator|&&
name|csiProto
operator|==
name|Csi
operator|.
name|NodePublishVolumeRequest
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|NodePublishVolumeRequestProtoTranslator
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|yarnProto
operator|==
name|GetPluginInfoResponse
operator|.
name|class
operator|&&
name|csiProto
operator|==
name|Csi
operator|.
name|GetPluginInfoResponse
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|GetPluginInfoResponseProtoTranslator
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|yarnProto
operator|==
name|NodeUnpublishVolumeRequest
operator|.
name|class
operator|&&
name|csiProto
operator|==
name|Csi
operator|.
name|NodeUnpublishVolumeRequest
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|NodeUnpublishVolumeRequestProtoTranslator
argument_list|()
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A problem is found while processing"
operator|+
literal|" proto message translating. Unexpected message types,"
operator|+
literal|" no transformer is found can handle the transformation from type "
operator|+
name|yarnProto
operator|.
name|getName
argument_list|()
operator|+
literal|"<-> "
operator|+
name|csiProto
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

