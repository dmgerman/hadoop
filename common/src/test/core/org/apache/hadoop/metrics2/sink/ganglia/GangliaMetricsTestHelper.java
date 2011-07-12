begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.sink.ganglia
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|sink
operator|.
name|ganglia
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|DatagramSocket
import|;
end_import

begin_comment
comment|/**  * Helper class in the same package as ganglia sinks to be used by unit tests  */
end_comment

begin_class
DECL|class|GangliaMetricsTestHelper
specifier|public
class|class
name|GangliaMetricsTestHelper
block|{
comment|/**    * Helper method to access package private method to set DatagramSocket    * needed for Unit test    * @param gangliaSink    * @param datagramSocket    */
DECL|method|setDatagramSocket (AbstractGangliaSink gangliaSink, DatagramSocket datagramSocket)
specifier|public
specifier|static
name|void
name|setDatagramSocket
parameter_list|(
name|AbstractGangliaSink
name|gangliaSink
parameter_list|,
name|DatagramSocket
name|datagramSocket
parameter_list|)
block|{
name|gangliaSink
operator|.
name|setDatagramSocket
argument_list|(
name|datagramSocket
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

