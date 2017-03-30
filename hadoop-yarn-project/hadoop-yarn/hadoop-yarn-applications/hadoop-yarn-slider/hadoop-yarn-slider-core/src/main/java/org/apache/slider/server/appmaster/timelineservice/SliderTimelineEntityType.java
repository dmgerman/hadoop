begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.timelineservice
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|timelineservice
package|;
end_package

begin_comment
comment|/**  * Slider entities that are published to ATS.  */
end_comment

begin_enum
DECL|enum|SliderTimelineEntityType
specifier|public
enum|enum
name|SliderTimelineEntityType
block|{
comment|/**    * Used for publishing service entity information.    */
DECL|enumConstant|SERVICE_ATTEMPT
name|SERVICE_ATTEMPT
block|,
comment|/**    * Used for publishing component entity information.    */
DECL|enumConstant|COMPONENT
name|COMPONENT
block|,
comment|/**    * Used for publishing component instance entity information.    */
DECL|enumConstant|COMPONENT_INSTANCE
name|COMPONENT_INSTANCE
block|}
end_enum

end_unit

