begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.planner
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
operator|.
name|planner
package|;
end_package

begin_comment
comment|/**  * Planner takes a DiskBalancerVolumeSet, threshold and  * computes a series of steps that lead to an even data  * distribution between volumes of this DiskBalancerVolumeSet.  *  * The main classes of this package are steps and planner.  *  * Here is a high level view of how planner operates:  *  * DiskBalancerVolumeSet current = volumeSet;  *  * while(current.isBalancingNeeded(thresholdValue)) {  *  *   // Creates a plan , like move 20 GB data from v1 -> v2  *   Step step = planner.plan(current, thresholdValue);  *  *   // we add that to our plan  *   planner.addStep(current, step);  *  *   // Apply the step to current state of the diskSet to  *   //compute the next state  *   current = planner.apply(current, step);  * }  *  * //when we are done , return the list of steps  * return planner;  */
end_comment

end_unit

