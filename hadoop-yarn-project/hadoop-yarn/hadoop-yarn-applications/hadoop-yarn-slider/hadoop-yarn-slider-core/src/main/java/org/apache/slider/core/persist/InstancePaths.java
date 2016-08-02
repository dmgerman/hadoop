begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.persist
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
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
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|SliderKeys
import|;
end_import

begin_comment
comment|/**  * Build up all the paths of an instance relative to the supplied instance  * directory.  */
end_comment

begin_class
DECL|class|InstancePaths
specifier|public
class|class
name|InstancePaths
block|{
DECL|field|instanceDir
specifier|public
specifier|final
name|Path
name|instanceDir
decl_stmt|;
DECL|field|snapshotConfPath
specifier|public
specifier|final
name|Path
name|snapshotConfPath
decl_stmt|;
DECL|field|generatedConfPath
specifier|public
specifier|final
name|Path
name|generatedConfPath
decl_stmt|;
DECL|field|historyPath
specifier|public
specifier|final
name|Path
name|historyPath
decl_stmt|;
DECL|field|dataPath
specifier|public
specifier|final
name|Path
name|dataPath
decl_stmt|;
DECL|field|tmpPath
specifier|public
specifier|final
name|Path
name|tmpPath
decl_stmt|;
DECL|field|tmpPathAM
specifier|public
specifier|final
name|Path
name|tmpPathAM
decl_stmt|;
DECL|field|appDefPath
specifier|public
specifier|final
name|Path
name|appDefPath
decl_stmt|;
DECL|field|addonsPath
specifier|public
specifier|final
name|Path
name|addonsPath
decl_stmt|;
DECL|method|InstancePaths (Path instanceDir)
specifier|public
name|InstancePaths
parameter_list|(
name|Path
name|instanceDir
parameter_list|)
block|{
name|this
operator|.
name|instanceDir
operator|=
name|instanceDir
expr_stmt|;
name|snapshotConfPath
operator|=
operator|new
name|Path
argument_list|(
name|instanceDir
argument_list|,
name|SliderKeys
operator|.
name|SNAPSHOT_CONF_DIR_NAME
argument_list|)
expr_stmt|;
name|generatedConfPath
operator|=
operator|new
name|Path
argument_list|(
name|instanceDir
argument_list|,
name|SliderKeys
operator|.
name|GENERATED_CONF_DIR_NAME
argument_list|)
expr_stmt|;
name|historyPath
operator|=
operator|new
name|Path
argument_list|(
name|instanceDir
argument_list|,
name|SliderKeys
operator|.
name|HISTORY_DIR_NAME
argument_list|)
expr_stmt|;
name|dataPath
operator|=
operator|new
name|Path
argument_list|(
name|instanceDir
argument_list|,
name|SliderKeys
operator|.
name|DATA_DIR_NAME
argument_list|)
expr_stmt|;
name|tmpPath
operator|=
operator|new
name|Path
argument_list|(
name|instanceDir
argument_list|,
name|SliderKeys
operator|.
name|TMP_DIR_PREFIX
argument_list|)
expr_stmt|;
name|tmpPathAM
operator|=
operator|new
name|Path
argument_list|(
name|tmpPath
argument_list|,
name|SliderKeys
operator|.
name|AM_DIR_PREFIX
argument_list|)
expr_stmt|;
name|appDefPath
operator|=
operator|new
name|Path
argument_list|(
name|tmpPath
argument_list|,
name|SliderKeys
operator|.
name|APP_DEF_DIR
argument_list|)
expr_stmt|;
name|addonsPath
operator|=
operator|new
name|Path
argument_list|(
name|tmpPath
argument_list|,
name|SliderKeys
operator|.
name|ADDONS_DIR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"instance at "
operator|+
name|instanceDir
return|;
block|}
block|}
end_class

end_unit

