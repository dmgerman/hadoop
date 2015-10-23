begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * NullContext.java  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics.spi
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
operator|.
name|spi
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

begin_comment
comment|/**  * Null metrics context: a metrics context which does nothing.  Used as the  * default context, so that no performance data is emitted if no configuration  * data is found.  *   * @deprecated Use org.apache.hadoop.metrics2 package instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|NullContext
specifier|public
class|class
name|NullContext
extends|extends
name|AbstractMetricsContext
block|{
comment|/** Creates a new instance of NullContext */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|NullContext ()
specifier|public
name|NullContext
parameter_list|()
block|{   }
comment|/**    * Do-nothing version of startMonitoring    */
annotation|@
name|Override
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|startMonitoring ()
specifier|public
name|void
name|startMonitoring
parameter_list|()
block|{   }
comment|/**    * Do-nothing version of emitRecord    */
annotation|@
name|Override
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|emitRecord (String contextName, String recordName, OutputRecord outRec)
specifier|protected
name|void
name|emitRecord
parameter_list|(
name|String
name|contextName
parameter_list|,
name|String
name|recordName
parameter_list|,
name|OutputRecord
name|outRec
parameter_list|)
block|{}
comment|/**    * Do-nothing version of update    */
annotation|@
name|Override
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|update (MetricsRecordImpl record)
specifier|protected
name|void
name|update
parameter_list|(
name|MetricsRecordImpl
name|record
parameter_list|)
block|{   }
comment|/**    * Do-nothing version of remove    */
annotation|@
name|Override
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|remove (MetricsRecordImpl record)
specifier|protected
name|void
name|remove
parameter_list|(
name|MetricsRecordImpl
name|record
parameter_list|)
block|{   }
block|}
end_class

end_unit

