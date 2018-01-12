/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 #include <errno.h>
 #include <fcntl.h>
 #include <inttypes.h>
 #include <signal.h>
 #include <stdio.h>
 #include <stdlib.h>
 #include <string.h>
 #include <sys/stat.h>
 #include <sys/wait.h>
 #include <unistd.h>

 #include <gtest/gtest.h>
 #include <sstream>

 extern "C" {
 #include "utils/path-utils.h"
 }

 namespace ContainerExecutor {

 class TestPathUtils : public ::testing::Test {
 protected:
   virtual void SetUp() {

   }

   virtual void TearDown() {

   }
 };

 TEST_F(TestPathUtils, test_path_safety) {
   const char* input = "./../abc/";
   int flag = verify_path_safety(input);
   std::cout << "Testing input=" << input << "\n";
   ASSERT_FALSE(flag) << "Should failed\n";

   input = "abc/./cde";
   flag = verify_path_safety(input);
   std::cout << "Testing input=" << input << "\n";
   ASSERT_TRUE(flag) << "Should succeeded\n";

   input = "/etc/abc/cde/./x/./y";
   flag = verify_path_safety(input);
   std::cout << "Testing input=" << input << "\n";
   ASSERT_TRUE(flag) << "Should succeeded\n";
}

TEST_F(TestPathUtils, test_dir_exists) {
   const char* input = "/non/existent/dir";
   int flag = dir_exists(input);
   std::cout << "Testing input=" << input << "\n";
   ASSERT_NE(flag, 0) << "Should failed\n";

   input = "/";
   flag = dir_exists(input);
   std::cout << "Testing input=" << input << "\n";
   ASSERT_EQ(flag, 0) << "Should succeeded\n";
}

} // namespace ContainerExecutor