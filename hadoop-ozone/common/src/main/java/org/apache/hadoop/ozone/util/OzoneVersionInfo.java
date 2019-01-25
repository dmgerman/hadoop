begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|util
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ClassUtil
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
name|utils
operator|.
name|HddsVersionInfo
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
name|utils
operator|.
name|VersionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This class returns build information about Hadoop components.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|OzoneVersionInfo
specifier|public
specifier|final
class|class
name|OzoneVersionInfo
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OzoneVersionInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|OZONE_VERSION_INFO
specifier|public
specifier|static
specifier|final
name|VersionInfo
name|OZONE_VERSION_INFO
init|=
operator|new
name|VersionInfo
argument_list|(
literal|"ozone"
argument_list|)
decl_stmt|;
DECL|method|OzoneVersionInfo ()
specifier|private
name|OzoneVersionInfo
parameter_list|()
block|{}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"                  //////////////                 \n"
operator|+
literal|"               ////////////////////              \n"
operator|+
literal|"            ////////     ////////////////        \n"
operator|+
literal|"           //////      ////////////////          \n"
operator|+
literal|"          /////      ////////////////  /         \n"
operator|+
literal|"         /////            ////////   ///         \n"
operator|+
literal|"         ////           ////////    /////        \n"
operator|+
literal|"        /////         ////////////////           \n"
operator|+
literal|"        /////       ////////////////   //        \n"
operator|+
literal|"         ////     ///////////////   /////        \n"
operator|+
literal|"         /////  ///////////////     ////         \n"
operator|+
literal|"          /////       //////      /////          \n"
operator|+
literal|"           //////   //////       /////           \n"
operator|+
literal|"             ///////////     ////////            \n"
operator|+
literal|"               //////  ////////////              \n"
operator|+
literal|"               ///   //////////                  \n"
operator|+
literal|"              /    "
operator|+
name|OZONE_VERSION_INFO
operator|.
name|getVersion
argument_list|()
operator|+
literal|"("
operator|+
name|OZONE_VERSION_INFO
operator|.
name|getRelease
argument_list|()
operator|+
literal|")\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Source code repository "
operator|+
name|OZONE_VERSION_INFO
operator|.
name|getUrl
argument_list|()
operator|+
literal|" -r "
operator|+
name|OZONE_VERSION_INFO
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Compiled by "
operator|+
name|OZONE_VERSION_INFO
operator|.
name|getUser
argument_list|()
operator|+
literal|" on "
operator|+
name|OZONE_VERSION_INFO
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Compiled with protoc "
operator|+
name|OZONE_VERSION_INFO
operator|.
name|getProtocVersion
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"From source with checksum "
operator|+
name|OZONE_VERSION_INFO
operator|.
name|getSrcChecksum
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"This command was run using "
operator|+
name|ClassUtil
operator|.
name|findContainingJar
argument_list|(
name|OzoneVersionInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|HddsVersionInfo
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

