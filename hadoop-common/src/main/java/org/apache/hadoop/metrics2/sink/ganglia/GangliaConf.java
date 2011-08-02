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
operator|.
name|AbstractGangliaSink
operator|.
name|GangliaSlope
import|;
end_import

begin_comment
comment|/**  * class which is used to store ganglia properties  */
end_comment

begin_class
DECL|class|GangliaConf
class|class
name|GangliaConf
block|{
DECL|field|units
specifier|private
name|String
name|units
init|=
name|AbstractGangliaSink
operator|.
name|DEFAULT_UNITS
decl_stmt|;
DECL|field|slope
specifier|private
name|GangliaSlope
name|slope
decl_stmt|;
DECL|field|dmax
specifier|private
name|int
name|dmax
init|=
name|AbstractGangliaSink
operator|.
name|DEFAULT_DMAX
decl_stmt|;
DECL|field|tmax
specifier|private
name|int
name|tmax
init|=
name|AbstractGangliaSink
operator|.
name|DEFAULT_TMAX
decl_stmt|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"unit="
argument_list|)
operator|.
name|append
argument_list|(
name|units
argument_list|)
operator|.
name|append
argument_list|(
literal|", slope="
argument_list|)
operator|.
name|append
argument_list|(
name|slope
argument_list|)
operator|.
name|append
argument_list|(
literal|", dmax="
argument_list|)
operator|.
name|append
argument_list|(
name|dmax
argument_list|)
operator|.
name|append
argument_list|(
literal|", tmax="
argument_list|)
operator|.
name|append
argument_list|(
name|tmax
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return the units    */
DECL|method|getUnits ()
name|String
name|getUnits
parameter_list|()
block|{
return|return
name|units
return|;
block|}
comment|/**    * @param units the units to set    */
DECL|method|setUnits (String units)
name|void
name|setUnits
parameter_list|(
name|String
name|units
parameter_list|)
block|{
name|this
operator|.
name|units
operator|=
name|units
expr_stmt|;
block|}
comment|/**    * @return the slope    */
DECL|method|getSlope ()
name|GangliaSlope
name|getSlope
parameter_list|()
block|{
return|return
name|slope
return|;
block|}
comment|/**    * @param slope the slope to set    */
DECL|method|setSlope (GangliaSlope slope)
name|void
name|setSlope
parameter_list|(
name|GangliaSlope
name|slope
parameter_list|)
block|{
name|this
operator|.
name|slope
operator|=
name|slope
expr_stmt|;
block|}
comment|/**    * @return the dmax    */
DECL|method|getDmax ()
name|int
name|getDmax
parameter_list|()
block|{
return|return
name|dmax
return|;
block|}
comment|/**    * @param dmax the dmax to set    */
DECL|method|setDmax (int dmax)
name|void
name|setDmax
parameter_list|(
name|int
name|dmax
parameter_list|)
block|{
name|this
operator|.
name|dmax
operator|=
name|dmax
expr_stmt|;
block|}
comment|/**    * @return the tmax    */
DECL|method|getTmax ()
name|int
name|getTmax
parameter_list|()
block|{
return|return
name|tmax
return|;
block|}
comment|/**    * @param tmax the tmax to set    */
DECL|method|setTmax (int tmax)
name|void
name|setTmax
parameter_list|(
name|int
name|tmax
parameter_list|)
block|{
name|this
operator|.
name|tmax
operator|=
name|tmax
expr_stmt|;
block|}
block|}
end_class

end_unit

