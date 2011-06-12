begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.vaidya.postexdiagnosis.tests
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|vaidya
operator|.
name|postexdiagnosis
operator|.
name|tests
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
name|vaidya
operator|.
name|statistics
operator|.
name|job
operator|.
name|JobStatistics
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
name|vaidya
operator|.
name|statistics
operator|.
name|job
operator|.
name|JobStatisticsInterface
operator|.
name|JobKeys
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
name|vaidya
operator|.
name|statistics
operator|.
name|job
operator|.
name|JobStatisticsInterface
operator|.
name|KeyDataType
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
name|vaidya
operator|.
name|statistics
operator|.
name|job
operator|.
name|JobStatisticsInterface
operator|.
name|ReduceTaskKeys
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
name|vaidya
operator|.
name|statistics
operator|.
name|job
operator|.
name|ReduceTaskStatistics
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
name|vaidya
operator|.
name|DiagnosticTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ReducesReExecutionImpact
specifier|public
class|class
name|ReducesReExecutionImpact
extends|extends
name|DiagnosticTest
block|{
DECL|field|_impact
specifier|private
name|double
name|_impact
decl_stmt|;
DECL|field|_job
specifier|private
name|JobStatistics
name|_job
decl_stmt|;
DECL|field|_percentReducesReExecuted
specifier|private
name|long
name|_percentReducesReExecuted
decl_stmt|;
comment|/**    *     */
DECL|method|ReducesReExecutionImpact ()
specifier|public
name|ReducesReExecutionImpact
parameter_list|()
block|{   }
comment|/*    * Evaluate the test        */
annotation|@
name|Override
DECL|method|evaluate (JobStatistics job)
specifier|public
name|double
name|evaluate
parameter_list|(
name|JobStatistics
name|job
parameter_list|)
block|{
comment|/*      * Set the this._job      */
name|this
operator|.
name|_job
operator|=
name|job
expr_stmt|;
comment|/* find job type */
if|if
condition|(
name|job
operator|.
name|getStringValue
argument_list|(
name|JobKeys
operator|.
name|JOBTYPE
argument_list|)
operator|.
name|equals
argument_list|(
literal|"MAP_ONLY"
argument_list|)
condition|)
block|{
name|this
operator|.
name|_impact
operator|=
literal|0
expr_stmt|;
return|return
name|this
operator|.
name|_impact
return|;
block|}
comment|/*      * Calculate and return the impact      */
name|this
operator|.
name|_impact
operator|=
operator|(
operator|(
name|job
operator|.
name|getLongValue
argument_list|(
name|JobKeys
operator|.
name|LAUNCHED_REDUCES
argument_list|)
operator|-
name|job
operator|.
name|getLongValue
argument_list|(
name|JobKeys
operator|.
name|TOTAL_REDUCES
argument_list|)
operator|)
operator|/
name|job
operator|.
name|getLongValue
argument_list|(
name|JobKeys
operator|.
name|TOTAL_REDUCES
argument_list|)
operator|)
expr_stmt|;
name|this
operator|.
name|_percentReducesReExecuted
operator|=
name|Math
operator|.
name|round
argument_list|(
name|this
operator|.
name|_impact
operator|*
literal|100
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|_impact
return|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.contrib.utils.perfadvisor.diagnostic_rules.DiagnosticRule#getAdvice()    */
annotation|@
name|Override
DECL|method|getPrescription ()
specifier|public
name|String
name|getPrescription
parameter_list|()
block|{
return|return
literal|"* Need careful evaluation of why reduce tasks are re-executed. \n"
operator|+
literal|"  * It could be due to some set of unstable cluster nodes.\n"
operator|+
literal|"  * It could be due application specific failures."
return|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.contrib.utils.perfadvisor.diagnostic_rules.DiagnosticRule#getReferenceDetails()    */
annotation|@
name|Override
DECL|method|getReferenceDetails ()
specifier|public
name|String
name|getReferenceDetails
parameter_list|()
block|{
name|String
name|ref
init|=
literal|"* Total Reduce Tasks: "
operator|+
name|this
operator|.
name|_job
operator|.
name|getLongValue
argument_list|(
name|JobKeys
operator|.
name|TOTAL_REDUCES
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"* Launched Reduce Tasks: "
operator|+
name|this
operator|.
name|_job
operator|.
name|getLongValue
argument_list|(
name|JobKeys
operator|.
name|LAUNCHED_REDUCES
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"* Percent Reduce Tasks ReExecuted: "
operator|+
name|this
operator|.
name|_percentReducesReExecuted
operator|+
literal|"\n"
operator|+
literal|"* Impact: "
operator|+
name|truncate
argument_list|(
name|this
operator|.
name|_impact
argument_list|)
decl_stmt|;
return|return
name|ref
return|;
block|}
block|}
end_class

end_unit

